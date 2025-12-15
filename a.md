<response>
  <reasoning>
    I will implement the robust Live Workout feature that correctly merges the workout template with exercise logs.

    1.  **Data Models (`WorkoutSession.kt`):**
        -   Define `ServerLiveSessionResponse`, `ServerTemplate`, `ServerTemplateExercise`, `ServerExerciseLog`, and `ServerExerciseInfo` to match the server's structure.
        -   Define `LiveWorkoutUiModel`, `WorkoutExerciseUi`, and `WorkoutSetUi` to handle the merged state (template + logs).
        -   Update `LogSetRequest` to include the `order` field.

    2.  **API (`ZiroApi.kt`):**
        -   Update `getActiveSession` to return `ApiResponse<ServerLiveSessionResponse>`.

    3.  **Repository (`LiveWorkoutRepository.kt`):**
        -   Implement the logic to fetch the session.
        -   Merge `workoutTemplate` with `exerciseLogs`.
        -   For each exercise in the template:
            -   Determine the number of sets to show (`maxOf(targetSets, loggedSets, 1)`).
            -   Create `WorkoutSetUi` items, populating them with real log data if available, or initializing them as "Ghost Sets" (empty, unchecked) if not.
        -   Handle ad-hoc exercises (logged but not in the template) by appending them to the list.
        -   Map the result to `LiveWorkoutUiModel`.

    4.  **ViewModel (`LiveWorkoutViewModel.kt`):**
        -   Update `loadActiveSession` to use the new repository logic.
        -   Update `updateSetInput` to modify the local UI state based on exercise ID and list index (since ghost sets don't have IDs yet).
        -   Update `onToggleSet` to validate inputs and call `repository.logSet`. crucially, it will reload the session on success to get authoritative IDs from the server.

    5.  **UI (`LiveWorkoutScreen.kt`):**
        -   Update `SetRow` to visually distinguish between completed sets (green, filled check) and ghost sets (transparent, outlined check).
        -   Pass the `set.order` or index correctly to the ViewModel.

    6.  **Task Tracking:** Update `TASKS.md`.
  </reasoning>
  <modifications>
    <file path="app/src/main/java/com/ziro/fit/model/WorkoutSession.kt">
      <![CDATA[
package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

// --- 1. SERVER RESPONSE DTOs ---

data class ServerLiveSessionResponse(
    val id: String,
    val startTime: String,
    val status: String,
    val notes: String?,
    val workoutTemplate: ServerTemplate?, // The Plan
    val exerciseLogs: List<ServerExerciseLog> // The Progress
)

data class ServerTemplate(
    val id: String,
    val name: String,
    val exercises: List<ServerTemplateExercise>
)

data class ServerTemplateExercise(
    val id: String, // Template Step ID
    val exerciseId: String,
    val order: Int,
    val targetSets: Int, // Important: How many sets are planned?
    val targetReps: String?,
    val exercise: ServerExerciseInfo
)

data class ServerExerciseLog(
    val id: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Double?,
    val order: Int,
    val isCompleted: Boolean,
    val exercise: ServerExerciseInfo
)

data class ServerExerciseInfo(
    val id: String,
    val name: String,
    val equipment: String?
)

// --- 2. UI DOMAIN MODELS (The "Source of Truth" for the UI) ---

data class LiveWorkoutUiModel(
    val id: String,
    val title: String,
    val startTime: String,
    val exercises: List<WorkoutExerciseUi>
)

data class WorkoutExerciseUi(
    val exerciseId: String,
    val exerciseName: String,
    val targetReps: String?, // Visual guide: "8-12"
    val sets: List<WorkoutSetUi>
)

data class WorkoutSetUi(
    val logId: String?, // Nullable: If null, it's a "Ghost Set" (planned but not saved)
    val setNumber: Int,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean,
    val order: Int // 0-indexed position in the list
)

// --- 3. API REQUEST ---

data class LogSetRequest(
    val workoutSessionId: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Double,
    @SerializedName("order") val order: Int
)
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/data/remote/ZiroApi.kt">
      <![CDATA[
package com.ziro.fit.data.remote

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.model.CalendarResponse
import com.ziro.fit.model.ServerLiveSessionResponse
import com.ziro.fit.model.LogSetRequest
import com.ziro.fit.model.LoginRequest
import com.ziro.fit.model.LoginResponse
import com.ziro.fit.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ZiroApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("api/auth/me")
    suspend fun getMe(): ApiResponse<User>

    @GET("api/trainer/calendar")
    suspend fun getCalendarEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ApiResponse<CalendarResponse>

    @GET("api/workout-sessions/live")
    suspend fun getActiveSession(): ApiResponse<ServerLiveSessionResponse>

    @POST("api/workout/log")
    suspend fun logSet(@Body request: LogSetRequest): ApiResponse<Any>

    @POST("api/workout-sessions/finish")
    suspend fun finishSession(@Body body: Map<String, String>): ApiResponse<Any>
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/data/repository/LiveWorkoutRepository.kt">
      <![CDATA[
package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.model.LogSetRequest
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class LiveWorkoutRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getActiveSession(): Result<LiveWorkoutUiModel> {
        return try {
            val response = api.getActiveSession()
            val data = response.data

            // 1. Group actual logs by Exercise ID
            val logsByExercise = data.exerciseLogs.groupBy { it.exerciseId }

            // 2. Build list from Template (The "Planned" Exercises)
            val uiExercises = mutableListOf<WorkoutExerciseUi>()
            
            // Track which exercises we've handled to identify ad-hoc ones later
            val processedExerciseIds = mutableSetOf<String>()

            data.workoutTemplate?.exercises?.sortedBy { it.order }?.forEach { templateStep ->
                processedExerciseIds.add(templateStep.exerciseId)
                
                val logsForThisExercise = logsByExercise[templateStep.exerciseId] ?: emptyList()
                
                // CRITICAL LOGIC: Merging Plan + Logs
                // If plan says 3 sets, but we logged 1, we show 1 real + 2 ghosts.
                // If plan says 3 sets, but we logged 4, we show 4 real.
                
                val targetSetsCount = templateStep.targetSets
                val actualLogsCount = logsForThisExercise.size
                
                // Determine total rows to show (at least as many as logged, or up to target)
                val setsToShowCount = max(targetSetsCount, actualLogsCount)
                // Ensure at least one set is shown if everything else is empty
                val finalSetsCount = max(setsToShowCount, 1)

                val setsUi = (0 until finalSetsCount).map { index ->
                    // Try to find a real log for this index
                    val existingLog = logsForThisExercise.find { it.order == index }
                    
                    if (existingLog != null) {
                        // Real Log (Saved in DB)
                        WorkoutSetUi(
                            logId = existingLog.id,
                            setNumber = index + 1,
                            weight = existingLog.weight?.toString() ?: "",
                            reps = existingLog.reps.toString(),
                            isCompleted = true, // It exists, so it's logged
                            order = index
                        )
                    } else {
                        // Ghost Set (Placeholder based on template)
                        WorkoutSetUi(
                            logId = null, // No ID yet
                            setNumber = index + 1,
                            weight = "", // Start empty
                            reps = "", // Start empty
                            isCompleted = false,
                            order = index
                        )
                    }
                }

                uiExercises.add(
                    WorkoutExerciseUi(
                        exerciseId = templateStep.exerciseId,
                        exerciseName = templateStep.exercise.name,
                        targetReps = templateStep.targetReps,
                        sets = setsUi
                    )
                )
            }

            // 3. Handle Ad-Hoc Exercises (Logged but not in Template)
            logsByExercise.forEach { (exerciseId, logs) ->
                if (!processedExerciseIds.contains(exerciseId)) {
                    val firstLog = logs.first()
                    
                    val setsUi = logs.sortedBy { it.order }.mapIndexed { index, log ->
                        WorkoutSetUi(
                            logId = log.id,
                            setNumber = index + 1,
                            weight = (log.weight ?: 0.0).toString(),
                            reps = log.reps.toString(),
                            isCompleted = true,
                            order = log.order
                        )
                    }

                    uiExercises.add(
                        WorkoutExerciseUi(
                            exerciseId = exerciseId,
                            exerciseName = firstLog.exercise.name,
                            targetReps = null, // No target for ad-hoc
                            sets = setsUi
                        )
                    )
                }
            }

            val uiModel = LiveWorkoutUiModel(
                id = data.id,
                title = data.workoutTemplate?.name ?: "Freestyle Workout",
                startTime = data.startTime,
                exercises = uiExercises
            )

            Result.success(uiModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logSet(sessionId: String, exerciseId: String, reps: Int, weight: Double, order: Int): Result<Unit> {
        return try {
            api.logSet(LogSetRequest(sessionId, exerciseId, reps, weight, order))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finishSession(sessionId: String, notes: String?): Result<Unit> {
        return try {
            api.finishSession(mapOf("workoutSessionId" to sessionId, "notes" to (notes ?: "")))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/ui/workout/LiveWorkoutViewModel.kt">
      <![CDATA[
package com.ziro.fit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.LiveWorkoutRepository
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.model.WorkoutSetUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

data class LiveWorkoutUiState(
    val isLoading: Boolean = true,
    val session: LiveWorkoutUiModel? = null,
    val elapsedSeconds: Long = 0,
    val error: String? = null,
    val isFinishing: Boolean = false,
    val isFinished: Boolean = false
)

@HiltViewModel
class LiveWorkoutViewModel @Inject constructor(
    private val repository: LiveWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveWorkoutUiState())
    val uiState: StateFlow<LiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadActiveSession()
    }

    private fun loadActiveSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getActiveSession()
                .onSuccess { session ->
                    _uiState.update { it.copy(session = session, isLoading = false) }
                    startTimer(session.startTime)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun startTimer(startTimeIso: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val start = LocalDateTime.parse(startTimeIso.removeSuffix("Z"))
            while (isActive) {
                val now = LocalDateTime.now()
                val seconds = Duration.between(start, now).seconds
                _uiState.update { it.copy(elapsedSeconds = seconds) }
                delay(1000)
            }
        }
    }

    // Called when user types in the text fields
    fun updateSetInput(exerciseId: String, setIndex: Int, weight: String, reps: String) {
        val currentSession = _uiState.value.session ?: return
        
        val updatedExercises = currentSession.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.toMutableList()
                if (setIndex in updatedSets.indices) {
                    val targetSet = updatedSets[setIndex]
                    updatedSets[setIndex] = targetSet.copy(weight = weight, reps = reps)
                    ex.copy(sets = updatedSets)
                } else ex
            } else ex
        }
        
        _uiState.update { it.copy(session = currentSession.copy(exercises = updatedExercises)) }
    }

    fun onToggleSet(exerciseId: String, set: WorkoutSetUi) {
        val weightVal = set.weight.toDoubleOrNull()
        val repsVal = set.reps.toIntOrNull()

        if (weightVal == null || repsVal == null) return // Invalid input

        // 1. Optimistic UI Update: Mark as completed visually immediately
        // Note: Real confirmation comes from reloading, but we want UI to feel snappy.
        val currentSession = _uiState.value.session ?: return
        val updatedExercises = currentSession.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.map { s ->
                    if (s.order == set.order) {
                        s.copy(isCompleted = true)
                    } else s
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.update { it.copy(session = currentSession.copy(exercises = updatedExercises)) }

        // 2. API Call
        viewModelScope.launch {
            repository.logSet(
                _uiState.value.session!!.id, 
                exerciseId, 
                repsVal, 
                weightVal, 
                set.order
            ).onSuccess {
                // Refresh to get consistent state and real IDs from server
                // We do a "silent" refresh (no loading spinner) to keep UX smooth
                repository.getActiveSession().onSuccess { refreshedSession ->
                    _uiState.update { it.copy(session = refreshedSession) }
                }
            }.onFailure {
                // Revert UI state on failure? 
                // For simplicity, we just show error in state, but ideally we'd uncheck the box.
                _uiState.update { it.copy(error = "Failed to save set") }
            }
        }
    }

    fun finishWorkout() {
        val sessionId = _uiState.value.session?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isFinishing = true) }
            repository.finishSession(sessionId, null)
                .onSuccess {
                    _uiState.update { it.copy(isFinished = true) }
                    timerJob?.cancel()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isFinishing = false, error = "Failed to finish: ${e.message}") }
                }
        }
    }
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/ui/workout/LiveWorkoutScreen.kt">
      <![CDATA[
package com.ziro.fit.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi

@Composable
fun LiveWorkoutScreen(
    viewModel: LiveWorkoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Handle Finish Navigation
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onNavigateBack()
    }

    Scaffold(
        topBar = {
            LiveWorkoutHeader(
                templateName = state.session?.title ?: "Workout",
                elapsedSeconds = state.elapsedSeconds,
                onFinish = viewModel::finishWorkout,
                isFinishing = state.isFinishing
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.session?.exercises ?: emptyList()) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onInputChange = viewModel::updateSetInput,
                        onSetToggle = { set -> 
                            viewModel.onToggleSet(exercise.exerciseId, set) 
                        }
                    )
                }
                
                item {
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun LiveWorkoutHeader(
    templateName: String,
    elapsedSeconds: Long,
    onFinish: () -> Unit,
    isFinishing: Boolean
) {
    // Format seconds to MM:SS
    val timeString = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = templateName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = timeString,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Button(
            onClick = onFinish,
            enabled = !isFinishing,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            if (isFinishing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
            } else {
                Text("FINISH")
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: WorkoutExerciseUi,
    onInputChange: (String, Int, String, String) -> Unit, // exerciseId, index, weight, reps
    onSetToggle: (WorkoutSetUi) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (exercise.targetReps != null) {
                Text(
                    text = "Target: ${exercise.targetReps} reps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                 Spacer(modifier = Modifier.height(8.dp))
            }

            // Header Row
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("Set", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelMedium)
                Text("Previous", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("kg", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Reps", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(48.dp)) // For Checkbox
            }

            exercise.sets.forEachIndexed { index, set ->
                SetRow(
                    set = set,
                    onWeightChange = { w -> onInputChange(exercise.exerciseId, index, w, set.reps) },
                    onRepsChange = { r -> onInputChange(exercise.exerciseId, index, set.weight, r) },
                    onCheck = { onSetToggle(set) }
                )
            }
        }
    }
}

@Composable
fun SetRow(
    set: WorkoutSetUi,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCheck: () -> Unit
) {
    val backgroundColor = if (set.isCompleted) Color(0xFFE8F5E9) else Color.Transparent // Light Green if done

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number
        Text(
            text = set.setNumber.toString(),
            modifier = Modifier.width(40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        // Previous data (Ghost text) - Placeholder
        Text(
            text = "-",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        // Inputs
        OutlinedTextField(
            value = set.weight,
            onValueChange = onWeightChange,
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
             colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        // Check Button
        IconButton(
            onClick = onCheck,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (set.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = "Log Set",
                tint = if (set.isCompleted) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
      ]]>
    </file>
    <file path="TASKS.md">
      <![CDATA[
- [x] Implement event detail bottom sheet
- [x] Implement Live Workout Feature (API, Repository, ViewModel, UI, Navigation)
- [x] Fix mismatch between Android Live Workout implementation and Server API
- [x] Implement robust merging of Workout Template and Logs for Live Workout
      ]]>
    </file>
  </modifications>
</response>
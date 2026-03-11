package com.ziro.fit.ui.workout

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi
import com.ziro.fit.ui.components.ExerciseBrowserContent
import com.ziro.fit.ui.theme.*
import com.ziro.fit.ui.workouts.WorkoutSuccessContent
import com.ziro.fit.viewmodel.WorkoutViewModel

enum class SessionFocusField { WEIGHT, REPS, RPE }
data class FocusTarget(val exerciseId: String, val setIndex: Int, val field: SessionFocusField)
enum class WorkoutInputOverlay { KEYBOARD, PLATE_CALCULATOR, RPE_PICKER, NONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkoutScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showExerciseSheet by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    
    val exerciseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val context = LocalContext.current

    // Advanced Input System State
    var focusedTarget by remember { mutableStateOf<FocusTarget?>(null) }
    var inputOverlay by remember { mutableStateOf(WorkoutInputOverlay.NONE) }
    var activeInputText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { viewModel.onPermissionsResult() }
    )
    
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = data?.get(0) ?: ""
            viewModel.parseVoiceCommand(text)
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
        viewModel.loadExercises()
    }

    LaunchedEffect(state.activeSession, state.isLoading, state.workoutSuccessStats) {
        if (state.activeSession == null && !state.isLoading && state.workoutSuccessStats == null) {
            onNavigateBack()
        }
    }

    fun syncInput() {
        focusedTarget?.let { target ->
            val exercise = state.activeSession?.exercises?.find { it.exerciseId == target.exerciseId }
            val set = exercise?.sets?.getOrNull(target.setIndex)
            if (set != null) {
                val weight = if (target.field == SessionFocusField.WEIGHT) activeInputText else set.weight
                val reps = if (target.field == SessionFocusField.REPS) activeInputText else set.reps
                viewModel.updateSetInput(target.exerciseId, target.setIndex, weight, reps)
            }
        }
    }

    fun triggerInput(target: FocusTarget) {
        syncInput()
        focusedTarget = target
        val exercise = state.activeSession?.exercises?.find { it.exerciseId == target.exerciseId }
        val set = exercise?.sets?.getOrNull(target.setIndex)
        activeInputText = if (target.field == SessionFocusField.WEIGHT) set?.weight ?: "" else set?.reps ?: ""
        inputOverlay = if (target.field == SessionFocusField.RPE) WorkoutInputOverlay.RPE_PICKER else WorkoutInputOverlay.KEYBOARD
    }

    fun handleNext() {
        syncInput()
        focusedTarget?.let { target ->
            val exercise = state.activeSession?.exercises?.find { it.exerciseId == target.exerciseId }
            val currentSet = exercise?.sets?.getOrNull(target.setIndex)

            if (target.field == SessionFocusField.WEIGHT) {
                triggerInput(FocusTarget(target.exerciseId, target.setIndex, SessionFocusField.REPS))
            } else {
                // Log the set automatically when moving past REPS
                if (currentSet != null) {
                    val updatedSet = currentSet.copy(
                        weight = if (target.field == SessionFocusField.WEIGHT) activeInputText else currentSet.weight,
                        reps = if (target.field == SessionFocusField.REPS) activeInputText else currentSet.reps
                    )
                    viewModel.logSet(target.exerciseId, updatedSet)
                }

                if (exercise != null && target.setIndex + 1 < exercise.sets.size) {
                    triggerInput(FocusTarget(target.exerciseId, target.setIndex + 1, SessionFocusField.WEIGHT))
                } else {
                    inputOverlay = WorkoutInputOverlay.NONE
                    focusedTarget = null
                }
            }
        }
    }

    val isBlank = state.activeSession?.exercises?.all { ex -> ex.sets.none { it.isCompleted } } ?: true

    Scaffold(
        containerColor = StrongBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(StrongBackground)
        ) {
            // Base layer tap to dismiss keyboard
            Box(modifier = Modifier.fillMaxSize().clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                syncInput()
                inputOverlay = WorkoutInputOverlay.NONE
                focusedTarget = null
            })

            Column(modifier = Modifier.fillMaxSize()) {
                LiveWorkoutHeader(
                    templateName = state.activeSession?.title ?: "Workout",
                    elapsedSeconds = state.elapsedSeconds,
                    isTimerRunning = true, // Default to true as explicit pause state is managed implicitly
                    isRestActive = state.isRestActive,
                    restSecondsRemaining = state.restSecondsRemaining,
                    onMinimize = {
                        syncInput()
                        onNavigateBack()
                    }
                )

                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StrongBlue)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 140.dp, top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        val exercises = state.activeSession?.exercises ?: emptyList()
                        if (exercises.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.Gray.copy(alpha = 0.3f))
                                    Spacer(Modifier.height(20.dp))
                                    Text("Start by adding an exercise", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                                    Spacer(Modifier.height(20.dp))
                                    Button(onClick = { showExerciseSheet = true }, colors = ButtonDefaults.buttonColors(containerColor = StrongBlue), shape = RoundedCornerShape(12.dp)) {
                                        Text("Add Exercise")
                                    }
                                }
                            }
                        } else {
                            itemsIndexed(exercises) { _, exercise ->
                                ExerciseCard(
                                    exercise = exercise,
                                    focusedTarget = focusedTarget,
                                    activeInputText = activeInputText,
                                    onTriggerInput = { triggerInput(it) },
                                    onSetToggle = { set -> 
                                        syncInput()
                                        viewModel.logSet(exercise.exerciseId, set) 
                                    },
                                    onAddSet = { viewModel.addSetToExercise(exercise.exerciseId) },
                                    onRemove = { /* ViewModel remove exercise not exposed yet, mock for now */ }
                                )
                            }
                            
                            item {
                                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Button(
                                        onClick = { showExerciseSheet = true },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StrongBlue.copy(alpha = 0.1f), contentColor = StrongBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircle, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Add Exercise", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Button(
                                        onClick = { showCancelDialog = true }, 
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StrongRed.copy(alpha = 0.1f), contentColor = StrongRed),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Cancel Workout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Bottom Controls (Hidden if keyboard open)
            if (inputOverlay == WorkoutInputOverlay.NONE) {
                LiveWorkoutControls(
                    isTimerRunning = true,
                    onTogglePause = { /* Implement pause logic if added to ViewModel */ },
                    onFinish = { 
                        if (isBlank) showCancelDialog = true 
                        else showFinishDialog = true 
                    },
                    isBlank = isBlank,
                    onMicTap = {
                        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        }
                        speechRecognizerLauncher.launch(intent)
                    }
                )
            }

            // Voice Command Overlay
            if (state.latestCommand != null) {
                val command = state.latestCommand!!
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.4f)).padding(16.dp).zIndex(400f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 120.dp),
                        colors = CardDefaults.cardColors(containerColor = StrongSecondaryBackground)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Voice Command Detected", color = Color.Gray, fontSize = 14.sp)
                                IconButton(onClick = { viewModel.dismissVoiceCommand() }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                                }
                            }
                            Text(command.exercise ?: "Active Exercise", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                if (command.sets != null) {
                                    Column { Text("Sets", color=Color.Gray, fontSize=12.sp); Text("${command.sets}", fontSize=16.sp, color=StrongTextPrimary) }
                                }
                                if (command.reps != null) {
                                    Column { Text("Reps", color=Color.Gray, fontSize=12.sp); Text("${command.reps}", fontSize=16.sp, color=StrongTextPrimary) }
                                }
                                if (command.weight != null) {
                                    Column { Text("Weight", color=Color.Gray, fontSize=12.sp); Text("${command.weight} kg", fontSize=16.sp, color=StrongTextPrimary) }
                                }
                            }
                            Button(onClick = { viewModel.confirmVoiceCommand() }, modifier = Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=StrongBlue)) {
                                Text("Confirm & Log")
                            }
                        }
                    }
                }
            }

            // Advanced Input Overlay
            if (inputOverlay != WorkoutInputOverlay.NONE) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(modifier = Modifier.fillMaxSize().clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        syncInput()
                        inputOverlay = WorkoutInputOverlay.NONE
                        focusedTarget = null
                    })
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(StrongSecondaryBackground)
                            .padding(bottom = 20.dp, top = 8.dp)
                    ) {
                        when (inputOverlay) {
                            WorkoutInputOverlay.KEYBOARD -> {
                                CustomNumericKeyboard(
                                    text = activeInputText,
                                    onTextChange = { activeInputText = it },
                                    onNext = { handleNext() },
                                    onDismiss = { 
                                        syncInput()
                                        inputOverlay = WorkoutInputOverlay.NONE
                                        focusedTarget = null
                                    },
                                    onAction = {
                                        inputOverlay = if (focusedTarget?.field == SessionFocusField.WEIGHT) WorkoutInputOverlay.PLATE_CALCULATOR else WorkoutInputOverlay.RPE_PICKER
                                    },
                                    isWeight = focusedTarget?.field == SessionFocusField.WEIGHT
                                )
                            }
                            WorkoutInputOverlay.PLATE_CALCULATOR -> {
                                PlateCalculatorOverlay(
                                    weightText = activeInputText,
                                    onWeightChange = { activeInputText = it },
                                    onBack = { inputOverlay = WorkoutInputOverlay.KEYBOARD }
                                )
                            }
                            WorkoutInputOverlay.RPE_PICKER -> {
                                val currentRpe = focusedTarget?.let { target ->
                                    state.activeSession?.exercises?.find { it.exerciseId == target.exerciseId }?.sets?.getOrNull(target.setIndex)?.rpe
                                }
                                RPEPickerOverlay(
                                    currentRpe = currentRpe,
                                    onRpeSelected = { rpe ->
                                        focusedTarget?.let { target ->
                                            viewModel.updateSetRpe(target.exerciseId, target.setIndex, rpe)
                                        }
                                        syncInput()
                                        inputOverlay = WorkoutInputOverlay.NONE
                                        focusedTarget = null
                                    },
                                    onDismiss = {
                                        syncInput()
                                        inputOverlay = WorkoutInputOverlay.NONE
                                        focusedTarget = null
                                    }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }

            if (showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text("Cancel Workout?") },
                    text = { Text("This will permanently discard your progress and logged sets for this session.") },
                    confirmButton = {
                        TextButton(onClick = { 
                            showCancelDialog = false
                            viewModel.cancelWorkout() 
                        }) {
                            Text("Discard", color = StrongRed)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog = false }) {
                            Text("No", color = StrongTextPrimary)
                        }
                    },
                    containerColor = StrongSurface,
                    titleContentColor = StrongTextPrimary,
                    textContentColor = StrongTextSecondary
                )
            }

            if (showFinishDialog) {
                AlertDialog(
                    onDismissRequest = { showFinishDialog = false },
                    title = { Text("Finish Workout?") },
                    text = { Text("Are you ready to log this session? Unfinished valid sets will be completed automatically.") },
                    confirmButton = {
                        Button(
                            onClick = { 
                                showFinishDialog = false
                                viewModel.finishWorkout() 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StrongGreen)
                        ) {
                            Text("Finish Workout")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFinishDialog = false }) {
                            Text("Cancel", color = StrongTextPrimary)
                        }
                    },
                    containerColor = StrongSurface,
                    titleContentColor = StrongTextPrimary,
                    textContentColor = StrongTextSecondary
                )
            }

            state.error?.let { errorMessage ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK", color = StrongBlue)
                        }
                    },
                    containerColor = StrongSurface,
                    titleContentColor = StrongRed,
                    textContentColor = StrongTextSecondary
                )
            }

            if (showExerciseSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showExerciseSheet = false },
                    sheetState = exerciseSheetState,
                    containerColor = StrongSurface
                ) {
                    ExerciseBrowserContent(
                        exercises = state.availableExercises,
                        isLoading = state.isExercisesLoading,
                        onSearch = viewModel::loadExercises,
                        onAddExercises = { exercises ->
                            viewModel.addExercisesToSession(exercises)
                            showExerciseSheet = false
                        }
                    )
                }
            }

            if (state.workoutSuccessStats != null) {
                ModalBottomSheet(
                    onDismissRequest = { 
                        viewModel.onSessionCompletedNavigated()
                    },
                    dragHandle = null,
                    containerColor = StrongSurface
                ) {
                    WorkoutSuccessContent(
                        stats = state.workoutSuccessStats!!,
                        onDone = {
                            viewModel.onSessionCompletedNavigated()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveWorkoutHeader(
    templateName: String,
    elapsedSeconds: Long,
    isTimerRunning: Boolean,
    isRestActive: Boolean,
    restSecondsRemaining: Int,
    onMinimize: () -> Unit
) {
    val timeString = if (elapsedSeconds >= 3600) {
        String.format("%d:%02d:%02d", elapsedSeconds / 3600, (elapsedSeconds % 3600) / 60, elapsedSeconds % 60)
    } else {
         String.format("%d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
    }

    Column(modifier = Modifier.fillMaxWidth().background(StrongBackground).pointerInput(Unit) {
        detectVerticalDragGestures { _, dragAmount ->
            if (dragAmount > 50) onMinimize()
        }
    }) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(width = 40.dp, height = 5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
                .align(Alignment.CenterHorizontally)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("PERSONAL SESSION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(templateName.uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary, maxLines = 1)
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(StrongSecondaryBackground)
                    .border(1.dp, StrongTextPrimary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isRestActive) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFFFA500), modifier = Modifier.size(14.dp))
                        Text(formatSeconds(restSecondsRemaining.toLong()), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFA500))
                    } else {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = StrongTextPrimary.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                        Text(timeString, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isTimerRunning) StrongGreen else Color(0xFFFFA500)))
                    }
                }
            }
        }
    }
}

@Composable
fun LiveWorkoutControls(
    isTimerRunning: Boolean,
    onTogglePause: () -> Unit,
    onFinish: () -> Unit,
    isBlank: Boolean,
    onMicTap: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(StrongSecondaryBackground.copy(alpha = 0.95f))
                .border(1.dp, StrongTextPrimary.copy(alpha = 0.05f), RoundedCornerShape(30.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onMicTap,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = StrongBlue),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
            }
            
            Button(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(StrongBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
            }
            
            Button(
                onClick = onTogglePause,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isTimerRunning) StrongBlue else Color(0xFFFFA500))
            ) {
                Icon(if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isTimerRunning) "Pause" else "Resume", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isBlank) StrongRed.copy(alpha = 0.8f) else StrongGreen)
            ) {
                Text(if (isBlank) "Cancel" else "Finish", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: WorkoutExerciseUi,
    focusedTarget: FocusTarget?,
    activeInputText: String,
    onTriggerInput: (FocusTarget) -> Unit,
    onSetToggle: (WorkoutSetUi) -> Unit,
    onAddSet: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(StrongSecondaryBackground)
            .border(1.dp, Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .padding(bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.exerciseName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(StrongBackground.copy(alpha = 0.5f))
                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("-", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray.copy(alpha=0.5f))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    }
                }

                Box {
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(StrongBackground.copy(alpha = 0.5f))
                            .clickable { expanded = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = StrongSecondaryBackground) {
                        DropdownMenuItem(text = { Text("Remove Exercise", color = StrongRed) }, onClick = { expanded = false; onRemove() })
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SET", modifier = Modifier.width(40.dp), color = Color.Gray.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("KG", modifier = Modifier.width(70.dp), color = Color.Gray.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Text("REPS", modifier = Modifier.width(65.dp), color = Color.Gray.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Text("RPE", modifier = Modifier.width(35.dp), color = Color.Gray.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(40.dp))
        }
        
        exercise.sets.forEachIndexed { index, set ->
            SetRow(
                exerciseId = exercise.exerciseId,
                setIndex = index,
                set = set,
                focusedTarget = focusedTarget,
                activeInputText = activeInputText,
                onTriggerInput = onTriggerInput,
                onToggle = { onSetToggle(set) }
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(StrongBlue.copy(alpha = 0.08f))
                .clickable { onAddSet() },
            contentAlignment = Alignment.Center
        ) {
            Text("+ Add Set", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StrongBlue)
        }
    }
}

@Composable
fun SetRow(
    exerciseId: String,
    setIndex: Int,
    set: WorkoutSetUi,
    focusedTarget: FocusTarget?,
    activeInputText: String,
    onTriggerInput: (FocusTarget) -> Unit,
    onToggle: () -> Unit
) {
    val isWeightFocused = focusedTarget?.exerciseId == exerciseId && focusedTarget.setIndex == setIndex && focusedTarget.field == SessionFocusField.WEIGHT
    val isRepsFocused = focusedTarget?.exerciseId == exerciseId && focusedTarget.setIndex == setIndex && focusedTarget.field == SessionFocusField.REPS

    val weightText = if (isWeightFocused && activeInputText.isNotEmpty()) activeInputText else if (set.weight.isEmpty() || set.weight == "0.0" || set.weight == "0") "-" else set.weight
    val repsText = if (isRepsFocused && activeInputText.isNotEmpty()) activeInputText else if (set.reps.isEmpty() || set.reps == "0") "-" else set.reps

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (set.isCompleted) StrongGreen.copy(alpha = 0.12f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (set.isCompleted) StrongGreen else Color.Gray.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(set.setNumber.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (set.isCompleted) Color.White else Color.Gray)
        }
        Spacer(Modifier.weight(1f))
        
        Box(
            modifier = Modifier
                .width(70.dp).height(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isWeightFocused) StrongSecondaryBackground else StrongInputBackground.copy(alpha = 0.3f))
                .border(if (isWeightFocused) 2.dp else 0.dp, if (isWeightFocused) StrongBlue else Color.Transparent, RoundedCornerShape(10.dp))
                .clickable { onTriggerInput(FocusTarget(exerciseId, setIndex, SessionFocusField.WEIGHT)) },
            contentAlignment = Alignment.Center
        ) {
            Text(weightText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
        }
        Spacer(Modifier.weight(1f))
        
        Box(
            modifier = Modifier
                .width(65.dp).height(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isRepsFocused) StrongSecondaryBackground else StrongInputBackground.copy(alpha = 0.3f))
                .border(if (isRepsFocused) 2.dp else 0.dp, if (isRepsFocused) StrongBlue else Color.Transparent, RoundedCornerShape(10.dp))
                .clickable { onTriggerInput(FocusTarget(exerciseId, setIndex, SessionFocusField.REPS)) },
            contentAlignment = Alignment.Center
        ) {
            Text(repsText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
        }
        Spacer(Modifier.weight(1f))
        
        Text("RPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (set.isCompleted) StrongGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (set.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = StrongGreen, modifier = Modifier.size(18.dp))
                } else {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.15f)))
                }
            }
        }
    }
}

@Composable
fun CustomNumericKeyboard(
    text: String,
    onTextChange: (String) -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    isWeight: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(modifier = Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "DEL")
            )
            for (row in keys) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (key in row) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StrongInputBackground.copy(alpha = 0.8f))
                                .clickable {
                                    if (key == "DEL") {
                                        if (text.isNotEmpty()) onTextChange(text.dropLast(1))
                                    } else {
                                        if (key == "." && text.contains(".")) return@clickable
                                        onTextChange(text + key)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "DEL") {
                                Text("DEL", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
                            } else {
                                Text(key, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = StrongTextPrimary)
                            }
                        }
                    }
                }
            }
        }
        
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(StrongSecondaryBackground).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = StrongTextPrimary)
            }
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(StrongSecondaryBackground).clickable { onAction() }, contentAlignment = Alignment.Center) {
                if (isWeight) {
                    Icon(Icons.Default.Apps, contentDescription = null, tint = StrongTextPrimary)
                } else {
                    Text("RPE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).height(60.dp).clip(RoundedCornerShape(8.dp)).background(StrongSecondaryBackground).clickable { 
                    val current = text.toDoubleOrNull() ?: 0.0
                    val decrement = if (isWeight) 1.25 else 1.0
                    val newVal = maxOf(0.0, current - decrement)
                    onTextChange(if (newVal == kotlin.math.floor(newVal)) newVal.toInt().toString() else newVal.toString())
                }, contentAlignment = Alignment.Center) {
                    Text("-", fontSize = 20.sp, color = StrongTextPrimary)
                }
                Box(modifier = Modifier.weight(1f).height(60.dp).clip(RoundedCornerShape(8.dp)).background(StrongSecondaryBackground).clickable { 
                    val current = text.toDoubleOrNull() ?: 0.0
                    val increment = if (isWeight) 1.25 else 1.0
                    val newVal = current + increment
                    onTextChange(if (newVal == kotlin.math.floor(newVal)) newVal.toInt().toString() else newVal.toString())
                }, contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 20.sp, color = StrongTextPrimary)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(StrongBlue).clickable { onNext() }, contentAlignment = Alignment.Center) {
                Text(if (isWeight) "Next" else "Log", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

private fun formatSeconds(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}

@Composable
fun PlateCalculatorOverlay(
    weightText: String,
    onWeightChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val barWeight = 20.0
    val currentWeight = weightText.toDoubleOrNull() ?: 0.0
    val availablePlates = listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25)

    fun addPlate(plate: Double) {
        val newWeight = if (currentWeight == 0.0) barWeight + (plate * 2) else currentWeight + (plate * 2)
        onWeightChange(if (newWeight == kotlin.math.floor(newWeight)) newWeight.toInt().toString() else newWeight.toString())
    }

    fun clearPlates() {
        onWeightChange(barWeight.toInt().toString())
    }

    fun calculatePlates(totalWeight: Double): List<Double> {
        var remaining = (totalWeight - barWeight) / 2.0
        val result = mutableListOf<Double>()
        for (plate in availablePlates) {
            while (remaining >= plate) {
                result.add(plate)
                remaining -= plate
            }
        }
        return result
    }

    val currentPlates = calculatePlates(currentWeight)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${if (currentWeight == kotlin.math.floor(currentWeight)) currentWeight.toInt() else currentWeight} kg",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = StrongTextPrimary
        )
        Text("Bar: ${barWeight.toInt()} kg", fontSize = 14.sp, color = StrongTextSecondary)

        Spacer(Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).background(Color.Gray, RoundedCornerShape(4.dp)))
            Row(modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(40.dp).height(20.dp).background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp)))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        currentPlates.reversed().forEach { plate -> PlateVisual(plate) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        currentPlates.forEach { plate -> PlateVisual(plate) }
                    }
                    Box(modifier = Modifier.width(40.dp).height(20.dp).background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp)))
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Tap plates to add", fontSize = 12.sp, color = StrongTextSecondary)
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            availablePlates.forEach { plate ->
                Button(
                    onClick = { addPlate(plate) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = plateColor(plate)),
                    modifier = Modifier.size(60.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (plate == kotlin.math.floor(plate)) plate.toInt().toString() else plate.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Button(
                onClick = { clearPlates() },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = StrongRed),
                modifier = Modifier.size(60.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = StrongSecondaryBackground)) {
            Text("Back to Keyboard", color = StrongTextPrimary)
        }
    }
}

@Composable
fun PlateVisual(weight: Double) {
    val height = when (weight) {
        25.0 -> 90.dp
        20.0 -> 90.dp
        15.0 -> 80.dp
        10.0 -> 70.dp
        5.0 -> 60.dp
        2.5 -> 50.dp
        1.25 -> 40.dp
        else -> 40.dp
    }
    val width = when (weight) {
        25.0 -> 12.dp
        20.0 -> 10.dp
        15.0 -> 10.dp
        10.0 -> 10.dp
        5.0 -> 8.dp
        2.5 -> 8.dp
        1.25 -> 8.dp
        else -> 8.dp
    }
    Box(modifier = Modifier.width(width).height(height).background(plateColor(weight), RoundedCornerShape(4.dp)))
}

fun plateColor(weight: Double): Color {
    return when (weight) {
        25.0 -> Color.Red
        20.0 -> Color.Blue
        15.0 -> Color.Yellow
        10.0 -> Color.Green
        5.0 -> Color.White.copy(alpha = 0.8f)
        2.5 -> Color.Red.copy(alpha = 0.6f)
        1.25 -> Color.Blue.copy(alpha = 0.6f)
        else -> Color.Gray
    }
}

@Composable
fun RPEPickerOverlay(
    currentRpe: Double?,
    onRpeSelected: (Double?) -> Unit,
    onDismiss: () -> Unit
) {
    val rpeValues = listOf(10.0, 9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 6.5, 6.0, 5.5, 5.0)

    fun rpeDescription(value: Double): String {
        return when (value) {
            10.0 -> "Max Effort"
            9.5 -> "Maybe 0 reps left"
            9.0 -> "1 rep left"
            8.5 -> "Maybe 1-2 reps left"
            8.0 -> "2 reps left"
            7.5 -> "Maybe 2-3 reps left"
            7.0 -> "3 reps left"
            6.5 -> "Maybe 3-4 reps left"
            6.0 -> "4+ reps left"
            5.5 -> "Warm up"
            5.0 -> "Light"
            else -> ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Select RPE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = StrongTextPrimary)
            TextButton(onClick = { onRpeSelected(null) }) {
                Text("Clear", color = StrongRed)
            }
        }

        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            items(rpeValues.size) { index ->
                val value = rpeValues[index]
                val isSelected = currentRpe == value
                Button(
                    onClick = { onRpeSelected(value) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) StrongBlue else StrongInputBackground.copy(alpha = 0.5f),
                        contentColor = if (isSelected) Color.White else StrongTextPrimary
                    ),
                    modifier = Modifier.width(80.dp).height(100.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(
                            text = if (value == kotlin.math.floor(value)) value.toInt().toString() else value.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = rpeDescription(value),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }

        Text("RPE (Rate of Perceived Exertion) helps track intensity.", fontSize = 12.sp, color = StrongTextSecondary)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = StrongSecondaryBackground)) {
            Text("Close", color = StrongTextPrimary)
        }
    }
}

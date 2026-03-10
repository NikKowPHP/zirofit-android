package com.ziro.fit.ui.workout

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
    
    val context = LocalContext.current

    // Advanced Input System State
    var focusedTarget by remember { mutableStateOf<FocusTarget?>(null) }
    var inputOverlay by remember { mutableStateOf(WorkoutInputOverlay.NONE) }
    var activeInputText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { viewModel.onPermissionsResult() }
    )

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
            if (target.field == SessionFocusField.WEIGHT) {
                triggerInput(FocusTarget(target.exerciseId, target.setIndex, SessionFocusField.REPS))
            } else {
                val exercise = state.activeSession?.exercises?.find { it.exerciseId == target.exerciseId }
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
                    isBlank = isBlank
                )
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
                                Column(modifier = Modifier.fillMaxWidth().height(300.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text("Plate Calculator (Coming Soon)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(20.dp))
                                    Button(onClick = { inputOverlay = WorkoutInputOverlay.KEYBOARD }, colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)) { 
                                        Text("Back to Keyboard") 
                                    }
                                }
                            }
                            WorkoutInputOverlay.RPE_PICKER -> {
                                Column(modifier = Modifier.fillMaxWidth().height(250.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text("RPE Picker (Coming Soon)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(20.dp))
                                    Button(onClick = { 
                                        syncInput()
                                        inputOverlay = WorkoutInputOverlay.NONE
                                        focusedTarget = null
                                    }, colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)) { 
                                        Text("Close") 
                                    }
                                }
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

            if (showExerciseSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showExerciseSheet = false },
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
    isBlank: Boolean
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
            Box(
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
                Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

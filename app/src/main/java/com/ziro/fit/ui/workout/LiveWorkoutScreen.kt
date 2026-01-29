package com.ziro.fit.ui.workout

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ziro.fit.model.Exercise
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi
import com.ziro.fit.ui.theme.*
import com.ziro.fit.ui.workouts.WorkoutSuccessContent
import com.ziro.fit.viewmodel.WorkoutViewModel
import com.ziro.fit.ui.components.ExerciseBrowserContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkoutScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showExerciseSheet by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Permission Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            viewModel.onPermissionsResult()
        }
    )

    // Request Notification & Activity Recognition Permission on entry
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

    // Handle back navigation if session is gone AND we are not showing success screen
    LaunchedEffect(state.activeSession, state.isLoading, state.workoutSuccessStats) {
        if (state.activeSession == null && !state.isLoading && state.workoutSuccessStats == null) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = StrongBackground, // Strong App Background
        topBar = {
            LiveWorkoutHeader(
                templateName = state.activeSession?.title ?: "Workout",
                elapsedSeconds = state.elapsedSeconds,
                onMinimize = onNavigateBack,
                onFinish = { viewModel.finishWorkout() },
                isFinishing = state.isFinishing
            )
        }
    ) { padding ->
        // Swipe to dismiss/minimize gesture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 50) { // Threshold for swipe down
                            onNavigateBack()
                        }
                    }
                }
        ) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StrongBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(state.activeSession?.exercises ?: emptyList()) { exercise ->
                        val isResting = state.isRestActive && state.restingExerciseId == exercise.exerciseId
                        
                        ExerciseCard(
                            exercise = exercise,
                            isResting = isResting,
                            restSecondsRemaining = state.restSecondsRemaining,
                            restTotalSeconds = state.restTotalSeconds,
                            onInputChange = viewModel::updateSetInput,
                            onSetToggle = { set -> viewModel.logSet(exercise.exerciseId, set) },
                            onAddSet = { viewModel.addSetToExercise(exercise.exerciseId) },
                            onAddRestTime = { viewModel.adjustRestTime(30) },
                            onSkipRest = { viewModel.stopRestTimer() }
                        )
                        HorizontalDivider(color = StrongDivider, thickness = 1.dp)
                    }
                    
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextButton(
                                onClick = { showExerciseSheet = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "ADD EXERCISE",
                                    color = StrongBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            TextButton(
                                onClick = { showCancelDialog = true }, 
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "CANCEL WORKOUT",
                                    color = StrongRed,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        
            // Confirmation Dialog
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
                    containerColor = StrongSurface
                )
            }
        
            // Exercise Browser Sheet
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

            // Success Sheet
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

// ... Keep existing composables (LiveWorkoutHeader, ExerciseCard, SetRow, etc.) ...
@Composable
fun LiveWorkoutHeader(
    templateName: String,
    elapsedSeconds: Long,
    onMinimize: () -> Unit,
    onFinish: () -> Unit,
    isFinishing: Boolean
) {
    // Format seconds to MM:SS or H:MM:SS
    val timeString = if (elapsedSeconds >= 3600) {
        String.format("%d:%02d:%02d", elapsedSeconds / 3600, (elapsedSeconds % 3600) / 60, elapsedSeconds % 60)
    } else {
         String.format("%d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrongSecondaryBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minimize Button (Left)
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Minimize",
            tint = StrongTextSecondary,
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = onMinimize)
        )

        Text(
            text = timeString,
            style = MaterialTheme.typography.titleLarge,
            color = StrongTextPrimary,
            fontWeight = FontWeight.Normal
        )

        // Finish Button (Right)
        if (isFinishing) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = StrongBlue, strokeWidth = 2.dp)
        } else {
            Text(
                text = "FINISH",
                color = StrongBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onFinish)
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: WorkoutExerciseUi,
    isResting: Boolean,
    restSecondsRemaining: Int,
    restTotalSeconds: Int,
    onInputChange: (String, Int, String, String) -> Unit, // exerciseId, index, weight, reps
    onSetToggle: (WorkoutSetUi) -> Unit,
    onAddSet: () -> Unit,
    onAddRestTime: () -> Unit,
    onSkipRest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrongBackground)
    ) {
        // Exercise Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = exercise.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StrongBlue,
                    fontSize = 18.sp
                )
            }
            
            // Options Menu (Placeholder)
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Options",
                tint = StrongBlue
            )
        }

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SET", modifier = Modifier.width(40.dp), color = StrongTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.width(8.dp))
            Text("PREVIOUS", modifier = Modifier.weight(1f), color = StrongTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("TARGET", modifier = Modifier.weight(0.7f), color = StrongTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("KG", modifier = Modifier.weight(1f), color = StrongTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("REPS", modifier = Modifier.weight(1f), color = StrongTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.width(40.dp)) // Checkbox column
        }

        // Sets
        exercise.sets.forEachIndexed { index, set ->
            SetRow(
                set = set,
                targetReps = exercise.targetReps,
                onWeightChange = { w -> onInputChange(exercise.exerciseId, index, w, set.reps) },
                onRepsChange = { r -> onInputChange(exercise.exerciseId, index, set.weight, r) },
                onCheck = { onSetToggle(set) }
            )
        }

        // "Add Set" Action
         Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAddSet)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ADD SET",
                color = StrongBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        // Inline Rest Timer (Only if active for this exercise)
        if (isResting) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StrongInputBackground) // Slightly lighter
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                 Text(
                    text = "Resting: ${restSecondsRemaining}s",
                    color = StrongTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { if (restTotalSeconds > 0) restSecondsRemaining.toFloat() / restTotalSeconds else 0f },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = StrongBlue,
                    trackColor = StrongDivider
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(onClick = onAddRestTime) { Text("+30s", color = StrongBlue) }
                    TextButton(onClick = onSkipRest) { Text("Skip", color = StrongTextPrimary) }
                }
            }
        }
    }
}

@Composable
fun SetRow(
    set: WorkoutSetUi,
    targetReps: String?,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCheck: () -> Unit
) {
    val backgroundColor = if (set.isCompleted) StrongGreen.copy(alpha = 0.2f) else Color.Transparent
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number
        Text(
            text = set.setNumber.toString(),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center,
            color = StrongTextSecondary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.width(8.dp))

        // Previous Data
        Text(
            text = "-", // Placeholder for "Previous"
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = StrongTextSecondary,
            fontSize = 14.sp
        )

        // Target Reps
        Text(
            text = targetReps ?: "-",
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.Center,
            color = StrongTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        // Inputs
        // Weight
        CompactInput(
            value = set.weight,
            onValueChange = onWeightChange,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
        
        Spacer(Modifier.width(8.dp))

        // Reps
        CompactInput(
            value = set.reps,
            onValueChange = onRepsChange,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = {
                onCheck()
                keyboardController?.hide()
                focusManager.clearFocus() 
            })
        )

        Spacer(Modifier.width(16.dp)) // Space before checkbox

        // Custom Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (set.isCompleted) StrongGreen else Color.Transparent)
                .border(2.dp, if (set.isCompleted) StrongGreen else StrongTextSecondary.copy(alpha=0.5f), RoundedCornerShape(4.dp))
                .clickable { 
                    onCheck()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
            contentAlignment = Alignment.Center
        ) {
            if (set.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CompactInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(StrongInputBackground),
        textStyle = LocalTextStyle.current.copy(
            color = StrongTextPrimary, 
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                innerTextField()
            }
        }
    )
}

package com.ziro.fit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ziro.fit.ui.calendar.CalendarScreen
import com.ziro.fit.ui.workout.LiveWorkoutScreen
import com.ziro.fit.ui.workout.LiveWorkoutMiniPlayer
import com.ziro.fit.ui.theme.ZirofitTheme
import com.ziro.fit.viewmodel.AuthState
import com.ziro.fit.viewmodel.AuthViewModel
import com.ziro.fit.viewmodel.UserViewModel
import com.ziro.fit.viewmodel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZirofitTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    val state = authViewModel.authState

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (state) {
            is AuthState.Loading -> LoadingScreen()
            is AuthState.Unauthenticated -> LoginScreen(onLogin = authViewModel::login, error = (state as? AuthState.Error)?.message)
            is AuthState.Authenticated -> MainAppScreen(onLogout = authViewModel::logout)
            is AuthState.Error -> LoginScreen(onLogin = authViewModel::login, error = state.message)
        }
    }
}

@Composable
fun MainAppScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    // Use the shared ViewModel for global workout state
    val workoutViewModel: WorkoutViewModel = hiltViewModel()
    val workoutState by workoutViewModel.uiState.collectAsState()
    
    // Refresh active session when the main app screen is loaded (e.g. after login or app restart)
    LaunchedEffect(Unit) {
        workoutViewModel.refreshActiveSession()
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Handle session completion navigation
    LaunchedEffect(workoutState.isSessionCompleted) {
        if (workoutState.isSessionCompleted) {
            // If completed, ensure we aren't stuck on the live workout screen
            if (currentRoute == "live_workout") {
                navController.popBackStack("calendar", inclusive = false)
            }
            workoutViewModel.onSessionCompletedNavigated()
        }
    }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                // Only show bottom bar for main tabs
                if (currentRoute != "live_workout") {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            label = { Text("Calendar") },
                            selected = currentRoute == "calendar",
                            onClick = {
                                navController.navigate("calendar") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text("Clients") },
                            selected = currentRoute == "clients",
                            onClick = {
                                navController.navigate("clients") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text("Profile") },
                            selected = currentRoute == "profile",
                            onClick = {
                                navController.navigate("profile") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "calendar",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("calendar") {
                    CalendarScreen(
                        workoutViewModel = workoutViewModel,
                        onNavigateToLiveWorkout = {
                            navController.navigate("live_workout")
                        }
                    )
                }
                composable("clients") {
                    com.ziro.fit.ui.client.ClientsScreen()
                }
                composable("profile") {
                    ProfileScreen(onLogout = onLogout)
                }
                composable("live_workout") {
                    LiveWorkoutScreen(
                        viewModel = workoutViewModel, // Pass the shared instance
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }

        // Floating Mini Player (Always alive if session exists and not on the workout screen)
        val isMiniPlayerVisible = workoutState.activeSession != null && currentRoute != "live_workout"
        if (isMiniPlayerVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp) // Initial position above nav bar
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                LiveWorkoutMiniPlayer(
                    isVisible = true,
                    sessionTitle = workoutState.activeSession?.title ?: "Active Workout",
                    elapsedSeconds = workoutState.elapsedSeconds,
                    onExpand = {
                        navController.navigate("live_workout") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, error: String? = null) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ZIRO.FIT Login", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Password") }, 
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error != null) {
            Text(text = error, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }
    }
}

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val user = userViewModel.user
    val isLoading = userViewModel.isLoading

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (user != null) {
            Text("Welcome back,", style = MaterialTheme.typography.titleMedium)
            Text(user.name ?: user.email, style = MaterialTheme.typography.headlineLarge)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Email: ${user.email}")
            Text("Role: ${user.role ?: "N/A"}")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Logout")
            }
        } else {
            Text("Failed to load user data")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) { Text("Logout") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { userViewModel.fetchUserProfile() }) { Text("Retry") }
        }
    }
}
      
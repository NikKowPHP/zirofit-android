package com.ziro.fit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.ziro.fit.ui.calendar.CalendarScreen
import com.ziro.fit.ui.workout.LiveWorkoutScreen
import com.ziro.fit.ui.workout.LiveWorkoutMiniPlayer
import com.ziro.fit.ui.theme.ZirofitTheme
import com.ziro.fit.ui.checkins.CheckInListScreen
import com.ziro.fit.ui.checkins.CheckInDetailScreen
import com.ziro.fit.ui.auth.RegisterScreen
import com.ziro.fit.ui.onboarding.RoleSelectionScreen
import com.ziro.fit.viewmodel.AuthState
import com.ziro.fit.viewmodel.AuthViewModel
import com.ziro.fit.viewmodel.UserViewModel
import com.ziro.fit.viewmodel.WorkoutViewModel



import com.ziro.fit.service.GlobalChatManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var globalChatManager: GlobalChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZirofitTheme {
                AppNavigation(globalChatManager = globalChatManager)
            }
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    globalChatManager: GlobalChatManager
) {
    val state = authViewModel.authState

    // Hook to initialize Chat Manager when authenticated
    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) {
            globalChatManager.initialize()
        } else if (state is AuthState.Unauthenticated) {
            globalChatManager.clear()
        }
    }
    
    // Manage root navigation state based on auth
    var currentScreen by remember { mutableStateOf<String>("loading") }

    LaunchedEffect(state) {
        currentScreen = when (state) {
            is AuthState.Loading -> "loading"
            is AuthState.Unauthenticated -> "login"
            is AuthState.Authenticated -> {
                if (!state.isOnboardingComplete) "onboarding" 
                else if (state.role == "client") "client_app" 
                else "trainer_app"
            }
            is AuthState.Error -> "login"
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (currentScreen) {
            "loading" -> LoadingScreen()
            "login" -> AuthNavigation(authViewModel = authViewModel, initialRoute = "login")
            "onboarding" -> RoleSelectionScreen(
                onOnboardingComplete = { role -> 
                    authViewModel.completeLocalOnboarding(role) 
                }
            )
            "client_app" -> ClientAppScreen(authViewModel = authViewModel, globalChatManager = globalChatManager)
            "trainer_app" -> MainAppScreen(onLogout = authViewModel::logout)
        }
    }
}

// Separate nav host for Auth (Login <-> Register)
@Composable
fun AuthNavigation(authViewModel: AuthViewModel, initialRoute: String) {
    val navController = rememberNavController()
    val isLoading = authViewModel.uiLoading
    val error = authViewModel.uiError
    
    NavHost(navController = navController, startDestination = initialRoute) {
        composable("login") {
            LoginScreen(
                onLogin = authViewModel::login, 
                onNavigateToRegister = { 
                    authViewModel.clearError()
                    navController.navigate("register") 
                },
                onClearError = authViewModel::clearError,
                isLoading = isLoading,
                error = error
            )
        }
        composable("register") {
             RegisterScreen(
                 onRegister = authViewModel::register,
                 onNavigateToLogin = { 
                     authViewModel.clearError()
                     navController.popBackStack() 
                 },
                 onClearError = authViewModel::clearError,
                 isLoading = isLoading,
                 error = error
             )
        }
    }
}

@Composable
fun ClientAppScreen(
    authViewModel: AuthViewModel,
    globalChatManager: GlobalChatManager // Passed down
) {
    val navController = rememberNavController()
    // Shared workout viewmodel for client too
    val workoutViewModel: WorkoutViewModel = hiltViewModel()
    val workoutState by workoutViewModel.uiState.collectAsState()

    // Refresh active session on load
    LaunchedEffect(Unit) {
        workoutViewModel.refreshActiveSession()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = {
                if (currentRoute != "live_workout") {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Menu, contentDescription = null) }, // Using generic icon for dashboard
                            label = { Text("Dashboard") },
                            selected = currentRoute == "client_dashboard",
                            onClick = {
                                navController.navigate("client_dashboard") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = null) }, // Using DateRange as placeholder for Workouts
                            label = { Text("Workouts") },
                            selected = currentRoute == "client_workouts",
                            onClick = {
                                navController.navigate("client_workouts") {
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
                startDestination = "client_dashboard",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("client_dashboard") {
                    com.ziro.fit.ui.dashboard.ClientDashboardScreen(
                        onLogout = authViewModel::logout,
                        onNavigateToDiscovery = { navController.navigate("trainer_discovery") },
                        onNavigateToCheckIns = { navController.navigate("client_checkins") },
                        onNavigateToLiveWorkout = {
                            navController.navigate("live_workout") {
                                launchSingleTop = true 
                            }
                        },
                        onNavigateToChat = { clientId, trainerId ->
                             navController.navigate("chat/$clientId/$trainerId")
                        }
                    )
                }
                composable(
                    route = "payment_success",
                    deepLinks = listOf(navDeepLink { uriPattern = "zirofit://payment/success" })
                ) {
                    com.ziro.fit.ui.components.PaymentSuccessDialog(
                        onDismiss = { navController.navigate("client_dashboard") }
                    )
                }
                composable(
                    route = "payment_cancel",
                    deepLinks = listOf(navDeepLink { uriPattern = "zirofit://payment/cancel" })
                ) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
                composable("trainer_discovery") {
                    com.ziro.fit.ui.discovery.TrainerDiscoveryScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onTrainerClick = { trainerId -> navController.navigate("trainer_profile/$trainerId") }
                    )
                }
                composable(
                    "trainer_profile/{trainerId}",
                    arguments = listOf(navArgument("trainerId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val trainerId = backStackEntry.arguments?.getString("trainerId") ?: ""
                    com.ziro.fit.ui.discovery.TrainerPublicProfileScreen(
                        trainerId = trainerId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("client_workouts") {
                    com.ziro.fit.ui.workouts.WorkoutsScreen(
                        onStartFreestyleWorkout = {
                            workoutViewModel.startWorkout(null, null, null)
                            navController.navigate("live_workout")
                        }
                    )
                }
                composable("live_workout") {
                    LiveWorkoutScreen(
                        viewModel = workoutViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("client_checkins") {
                    com.ziro.fit.ui.checkins.ClientCheckInListScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDetail = { id -> navController.navigate("client_checkins_detail/$id") },
                        onNavigateToSubmit = { navController.navigate("client_checkins_submit") }
                    )
                }
                composable("client_checkins_submit") {
                    com.ziro.fit.ui.checkins.CheckInSubmissionScreen(
                        onNavigateBack = { 
                            navController.popBackStack()
                            // Maybe pop back to list to refresh? Default behavior should handle it?
                            // Actually CheckInSubmissionScreen calls onNavigateBack on success. 
                            // Ideally it goes back to list which refreshes on launch effect.
                        }
                    )
                }
                composable(
                    route = "client_checkins_detail/{checkInId}",
                    arguments = listOf(navArgument("checkInId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val checkInId = backStackEntry.arguments?.getString("checkInId") ?: ""
                    com.ziro.fit.ui.checkins.ClientCheckInDetailScreen(
                        checkInId = checkInId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("profile") {
                    com.ziro.fit.ui.profile.ClientProfileScreen(
                        onLogout = { authViewModel.logout() }
                    )
                }
                composable(
                    route = "chat/{clientId}/{trainerId}",
                    arguments = listOf(
                        navArgument("clientId") { type = NavType.StringType },
                        navArgument("trainerId") { type = NavType.StringType }
                    )
                ) {
                    DisposableEffect(Unit) {
                        onDispose {
                            globalChatManager.onChatClosed()
                        }
                    }

                    com.ziro.fit.ui.chat.ChatScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToLiveWorkout = {
                             navController.navigate("live_workout") {
                                launchSingleTop = true
                             }
                        }
                    )
                }
            }
        }
        
         // Floating Mini Player (Client)
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
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                            label = { Text("More") },
                            selected = currentRoute == "more",
                            onClick = {
                                navController.navigate("more") {
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
                        },
                        onNavigateToCreateSession = { date ->
                            navController.navigate("create_session?date=$date")
                        }
                    )
                }
                composable("clients") {
                    com.ziro.fit.ui.client.ClientsScreen(
                        onClientClick = { clientId ->
                            navController.navigate("client_details/$clientId")
                        }
                    )
                }
                composable(
                    route = "client_details/{clientId}",
                    arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
                    com.ziro.fit.ui.client.ClientDetailsScreen(
                        clientId = clientId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToMeasurements = { id -> navController.navigate("client_details/$id/measurements") },
                        onNavigateToAssessments = { id -> navController.navigate("client_details/$id/assessments") },
                        onNavigateToPhotos = { id -> navController.navigate("client_details/$id/photos") },
                        onNavigateToSessions = { id -> navController.navigate("client_details/$id/sessions") },
                        onNavigateToChat = { id -> navController.navigate("chat/$id/me") }
                    )
                }
                composable(
                    route = "client_details/{clientId}/measurements",
                    arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
                    com.ziro.fit.ui.client.ClientMeasurementsScreen(
                        clientId = clientId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "client_details/{clientId}/assessments",
                    arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
                    com.ziro.fit.ui.client.ClientAssessmentsScreen(
                        clientId = clientId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "client_details/{clientId}/photos",
                    arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
                    com.ziro.fit.ui.client.ClientPhotosScreen(
                        clientId = clientId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "client_details/{clientId}/sessions",
                    arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
                    com.ziro.fit.ui.client.ClientSessionsScreen(
                        clientId = clientId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("profile") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.ProfileScreen(
                        onLogout = onLogout,
                        onNavigateToSubScreen = { route -> navController.navigate(route) }
                    )
                }
                composable("profile/core_info") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.CoreInfoScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/branding") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.BrandingScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/services") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.ServicesScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/packages") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.PackagesScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/availability") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.AvailabilityScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/transformation_photos") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.TransformationPhotosScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/testimonials") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.TestimonialsScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/social_links") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.SocialLinksScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/external_links") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.ExternalLinksScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/billing") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.BillingScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/benefits") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.BenefitsScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("profile/notifications") {
                    val profileViewModel: com.ziro.fit.viewmodel.ProfileViewModel = hiltViewModel()
                    com.ziro.fit.ui.profile.subscreens.NotificationsScreen(viewModel = profileViewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("live_workout") {
                    LiveWorkoutScreen(
                        viewModel = workoutViewModel, // Pass the shared instance
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "create_session?date={date}",
                    arguments = listOf(navArgument("date") { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
                ) { backStackEntry ->
                    val dateString = backStackEntry.arguments?.getString("date")
                    val initialDate = dateString?.let { 
                        try {
                            java.time.LocalDate.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    com.ziro.fit.ui.calendar.CreateSessionScreen(
                        initialDate = initialDate,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("more") {
                    com.ziro.fit.ui.more.MoreScreen(
                        onNavigateToAssessments = { navController.navigate("assessments_library") },
                        onNavigateToBookings = { navController.navigate("bookings_list") },
                        onNavigateToCheckIns = { navController.navigate("checkins_list") }
                    )
                }
                composable("assessments_library") {
                    com.ziro.fit.ui.more.AssessmentsLibraryScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToCreate = { navController.navigate("assessments_create") },
                        onNavigateToEdit = { id -> navController.navigate("assessments_edit/$id") }
                    )
                }
                composable("assessments_create") {
                    com.ziro.fit.ui.more.CreateEditAssessmentScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "assessments_edit/{assessmentId}",
                    arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val assessmentId = backStackEntry.arguments?.getString("assessmentId")
                    com.ziro.fit.ui.more.CreateEditAssessmentScreen(
                        onNavigateBack = { navController.popBackStack() },
                        assessmentId = assessmentId
                    )
                }
                composable("bookings_list") {
                    com.ziro.fit.ui.bookings.BookingsListScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToCreate = { navController.navigate("bookings_create") },
                        onNavigateToEdit = { id -> navController.navigate("bookings_edit/$id") }
                    )
                }
                composable("bookings_create") {
                    com.ziro.fit.ui.bookings.CreateEditBookingScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "bookings_edit/{bookingId}",
                    arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val bookingId = backStackEntry.arguments?.getString("bookingId")
                    com.ziro.fit.ui.bookings.CreateEditBookingScreen(
                        onNavigateBack = { navController.popBackStack() },
                        bookingId = bookingId
                    )
                }
                composable("checkins_list") {
                    CheckInListScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDetail = { id -> navController.navigate("checkins_detail/$id") }
                    )
                }
                composable(
                    route = "checkins_detail/{checkInId}",
                    arguments = listOf(navArgument("checkInId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val checkInId = backStackEntry.arguments?.getString("checkInId") ?: ""
                    CheckInDetailScreen(
                        checkInId = checkInId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "chat/{clientId}/{trainerId}",
                    arguments = listOf(
                        navArgument("clientId") { type = NavType.StringType },
                        navArgument("trainerId") { type = NavType.StringType }
                    )
                ) {
                    com.ziro.fit.ui.chat.ChatScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToLiveWorkout = {
                             navController.navigate("live_workout") {
                                launchSingleTop = true
                             }
                        }
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
fun LoginScreen(
    onLogin: (String, String) -> Unit, 
    onNavigateToRegister: () -> Unit, 
    onClearError: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Clear error when typing
    LaunchedEffect(email, password) {
        onClearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back", 
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Sign in to continue to ZIRO.FIT",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Error Banner
        androidx.compose.animation.AnimatedVisibility(
            visible = error != null,
            enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = error ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Email Address") },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Password") }, 
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onLogin(email, password) }
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign In", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Sign Up",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


      
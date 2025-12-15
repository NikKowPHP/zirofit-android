<response>
  <reasoning>
    I will implement the Calendar feature following the proposed architecture:
    1.  **Dependencies**: Add `androidx.navigation:navigation-compose` to `libs.versions.toml` and `app/build.gradle.kts` to support the new tabbed navigation.
    2.  **Domain/Data**:
        *   Create `CalendarEvent` and `EventType` models in `com/ziro/fit/model/CalendarEvent.kt`.
        *   Update `ZiroApi.kt` to include the `getCalendarEvents` endpoint.
        *   Create `CalendarRepository` to handle data fetching and date logic, decoupling it from the ViewModel.
    3.  **State Management**: Create `CalendarViewModel` with a unified `CalendarUiState` for robust state handling (Loading, Content, Error).
    4.  **UI**:
        *   Create `CalendarScreen.kt` featuring a custom `WeekCalendarView` and `EventsList` using Jetpack Compose.
    5.  **Integration**:
        *   Refactor `MainActivity.kt` to introduce a `MainAppScreen` with a Bottom Navigation Bar (`Scaffold` + `NavigationBar`) to switch between "Calendar" and "Profile".
        *   The existing `DashboardScreen` will be reused as the "Profile" tab.
  </reasoning>
  <modifications>
    <file path="gradle/libs.versions.toml">
      <![CDATA[
[versions]
agp = "8.13.2"
kotlin = "2.0.21"
coreKtx = "1.17.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.10.0"
activityCompose = "1.12.1"
composeBom = "2024.09.00"
supabase = "2.5.0"
ktor = "2.3.12"
retrofit = "2.9.0"
okhttp = "4.12.0"
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"
navigationCompose = "2.8.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Supabase
supabase-gotrue = { group = "io.github.jan-tennert.supabase", name = "gotrue-kt", version.ref = "supabase" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabase" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }

# Retrofit
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
      ]]>
    </file>
    <file path="app/build.gradle.kts">
      <![CDATA[
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.ziro.fit"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.ziro.fit"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Secure Storage
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/model/CalendarEvent.kt">
      <![CDATA[
package com.ziro.fit.model

import java.time.LocalDateTime

data class CalendarEvent(
    val id: String,
    val title: String,
    val start: String, // Keep as String for GSON/Serialization
    val end: String,
    val type: EventType,
    val clientName: String?,
    val notes: String?
) {
    // Helper for UI logic
    val startTime: LocalDateTime get() = LocalDateTime.parse(start.removeSuffix("Z"))
    val endTime: LocalDateTime get() = LocalDateTime.parse(end.removeSuffix("Z"))
}

data class CalendarResponse(
    val events: List<CalendarEvent>
)

enum class EventType {
    booking,
    session_planned,
    session_completed,
    session_in_progress
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/data/remote/ZiroApi.kt">
      <![CDATA[
package com.ziro.fit.data.remote

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.model.CalendarResponse
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
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/data/repository/CalendarRepository.kt">
      <![CDATA[
package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.CalendarEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getEvents(date: LocalDate): Result<List<CalendarEvent>> {
        return try {
            // Fetch a buffer around the selected date (e.g., 2 weeks back, 2 weeks forward)
            // to allow smooth swiping without constant loading
            val start = date.minusWeeks(2).atStartOfDay()
            val end = date.plusWeeks(2).atTime(23, 59, 59)
            
            // Format to ISO 8601
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            
            val response = api.getCalendarEvents(
                startDate = start.format(formatter),
                endDate = end.format(formatter)
            )
            
            Result.success(response.data.events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/ui/calendar/CalendarViewModel.kt">
      <![CDATA[
package com.ziro.fit.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.CalendarRepository
import com.ziro.fit.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // Derived property: Filter events for the selected date on the UI side
    // This makes the UI snappy as switching days doesn't always need a network call
    val selectedDateEvents: List<CalendarEvent>
        get() = events.filter { 
            it.startTime.toLocalDate().isEqual(selectedDate) 
        }.sortedBy { it.startTime }
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        fetchEvents()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        // Optional: If date is outside current cache range, fetch more
        // For now, we fetch every time date changes or we could optimize
        // to only fetch if close to the edge of the buffer
        fetchEvents() 
    }

    fun retry() {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getEvents(_uiState.value.selectedDate)
                .onSuccess { fetchedEvents ->
                    _uiState.update { it.copy(events = fetchedEvents, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/ui/calendar/CalendarScreen.kt">
      <![CDATA[
package com.ziro.fit.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.CalendarEvent
import com.ziro.fit.model.EventType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // 1. Header (Month Name)
        Text(
            text = state.selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // 2. Custom Week View
        WeekCalendarView(
            selectedDate = state.selectedDate,
            onDateSelected = viewModel::onDateSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Events List
        if (state.isLoading && state.events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error loading events", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.retry() }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            EventsList(events = state.selectedDateEvents)
        }
    }
}

@Composable
fun WeekCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Simple logic to show 7 days centered roughly around selected date
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1)
    val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { date ->
            val isSelected = date.isEqual(selectedDate)
            val isToday = date.isEqual(LocalDate.now())
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDateSelected(date) }
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun EventsList(events: List<CalendarEvent>) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Text(
                text = "No sessions today",
                color = Color.Gray,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventItem(event)
        }
    }
}

@Composable
fun EventItem(event: CalendarEvent) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    // Status color mapping
    val statusColor = when(event.type) {
        EventType.session_in_progress -> Color(0xFFF59E0B) // Amber
        EventType.session_completed -> Color(0xFF10B981) // Green
        EventType.session_planned -> Color(0xFF6366F1) // Indigo
        EventType.booking -> Color(0xFFEC4899) // Pink
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            // Time Column
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(50.dp)) {
                Text(
                    text = event.startTime.format(timeFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.endTime.format(timeFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            
            // Vertical Divider
            Box(modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(statusColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (event.clientName != null) {
                    Text(
                        text = event.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/MainActivity.kt">
      <![CDATA[
package com.ziro.fit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.navigation.compose.rememberNavController
import com.ziro.fit.ui.calendar.CalendarScreen
import com.ziro.fit.ui.theme.ZirofitTheme
import com.ziro.fit.viewmodel.AuthState
import com.ziro.fit.viewmodel.AuthViewModel
import com.ziro.fit.viewmodel.UserViewModel
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
    // Simple state to track current screen for bottom bar highlighting
    var currentRoute by remember { mutableStateOf("calendar") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Calendar") },
                    selected = currentRoute == "calendar",
                    onClick = {
                        currentRoute = "calendar"
                        navController.navigate("calendar") {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = currentRoute == "profile",
                    onClick = {
                        currentRoute = "profile"
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "calendar",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("calendar") {
                CalendarScreen()
            }
            composable("profile") {
                ProfileScreen(onLogout = onLogout)
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
      ]]>
    </file>
    <file path="TASKS.md">
      <![CDATA[
- [x] Implement Calendar Tab with Week View
- [x] Create Calendar Repository and Models
- [x] Add Navigation Bottom Bar
      ]]>
    </file>
  </modifications>
</response>
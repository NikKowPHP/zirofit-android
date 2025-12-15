I will implement the Audio Translation feature by adding audio recording capabilities, setting up the Multipart API request, and updating the UI to support voice input.

1.  **Modify `AndroidManifest.xml`**: Add the `RECORD_AUDIO` permission.
2.  **Modify `LexityModels.kt`**: Add the `AudioTranslateResponse` data model.
3.  **Modify `NetworkModule.kt`**:
    *   Add the `audioTranslate` endpoint with `@Multipart` support.
    *   Increase OkHttp timeouts to 60 seconds as recommended for AI processing.
4.  **Create `AudioRecorder.kt`**: A helper class to manage `MediaRecorder` with the recommended settings (AAC, 16kHz, low bitrate).
5.  **Modify `MainActivity.kt`**:
    *   Add a Microphone button to the `TranslatorScreen`.
    *   Implement permission handling for `RECORD_AUDIO`.
    *   Implement recording state management (Recording vs. Idle).
    *   Handle the API call: upload the file, update the `inputText` with the transcription, and display the translation results.
6.  **Update `TASKS.md`**.

<modifications>
<file path="app/src/main/AndroidManifest.xml">
<![CDATA[
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:usesCleartextTraffic="true"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Translate">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Translate">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".BubbleActivity"
            android:allowEmbedded="true"
            android:documentLaunchMode="always"
            android:resizeableActivity="true"
            android:label="Lexity Translator"
            android:exported="true" />
    </application>

</manifest>
]]>
</file>

<file path="app/src/main/java/com/example/translate/LexityModels.kt">
<![CDATA[
package com.example.translate

import com.google.gson.annotations.SerializedName

// 1. Login
data class LoginRequest(val email: String, val password: String)
data class UserResponse(val user: UserData?, val session: SessionData?)
data class UserData(val id: String, val email: String)
data class SessionData(@SerializedName("access_token") val accessToken: String)

// 2. User Profile
data class UserProfileResponse(
    val id: String,
    val email: String,
    val nativeLanguage: String?, // e.g. "english"
    val defaultTargetLanguage: String?, // e.g. "spanish"
    val languageProfiles: List<LanguageProfileData>
)

data class LanguageProfileData(
    val language: String // e.g. "spanish"
)

data class UpdateProfileRequest(
    val targetLanguage: String // e.g. "french"
)

// 3. Translate
data class TranslateRequest(
    val text: String,
    val sourceLanguage: String,
    val targetLanguage: String
)

data class TranslateResponse(
    val fullTranslation: String,
    val segments: List<Segment>
)

data class AudioTranslateResponse(
    val sourceText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val translatedText: String,
    val segments: List<Segment>
)

data class Segment(
    val source: String,
    val translation: String,
    val explanation: String
)

// 4. Create Flashcard
data class FlashcardRequest(
    val frontContent: String,
    val backContent: String,
    val targetLanguage: String,
    val explanation: String,
    val type: String = "TRANSLATION"
)

// 5. Study Deck & Review
data class SrsItem(
    val id: String,
    val frontContent: String,
    val backContent: String,
    val context: String?,
    val type: String,          // "MISTAKE", "TRANSLATION", etc.
    val interval: Int,
    val easeFactor: Double,
    val targetLanguage: String?
)

data class ReviewRequest(
    val srsItemId: String,
    val quality: Int // 0=Again, 3=Hard, 4=Good, 5=Easy (Simplified logic: 0=Forgot, 3=Good, 5=Easy)
)
]]>
</file>

<file path="app/src/main/java/com/example/translate/NetworkModule.kt">
<![CDATA[
package com.example.translate

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// 1. Define the API Endpoints
interface LexityApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<UserResponse>

    @GET("/api/user/profile")
    suspend fun getUserProfile(): UserProfileResponse

    @PUT("/api/user/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): UserProfileResponse

    @POST("/api/ai/translate-breakdown")
    suspend fun translate(@Body request: TranslateRequest): TranslateResponse

    @Multipart
    @POST("/api/ai/audio-translate")
    suspend fun audioTranslate(
        @Part audio: MultipartBody.Part,
        @Part("sourceLanguage") sourceLanguage: RequestBody,
        @Part("targetLanguage") targetLanguage: RequestBody
    ): AudioTranslateResponse

    @POST("/api/srs/create-from-translation")
    suspend fun createFlashcard(@Body request: FlashcardRequest): Any

    @GET("/api/srs/deck")
    suspend fun getStudyDeck(
        @Query("targetLanguage") targetLanguage: String,
        @Query("includeAll") includeAll: Boolean = true
    ): List<SrsItem>

    // Fetch ONLY due cards (includeAll=false)
    @GET("/api/srs/deck")
    suspend fun getDueStudyDeck(
        @Query("targetLanguage") targetLanguage: String,
        @Query("includeAll") includeAll: Boolean = false
    ): List<SrsItem>

    @POST("/api/srs/review")
    suspend fun reviewCard(@Body request: ReviewRequest): Any
}

// 2. Singleton to hold the Retrofit instance
object RetrofitClient {
    // Basic in-memory cookie store to handle Auth
    private val cookieStore = HashMap<String, List<Cookie>>()

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }
    }

    // USE 10.0.2.2 FOR EMULATOR TO REACH LOCALHOST
//    private const val BASE_URL = "http://10.0.2.2:3555/"

    private const val BASE_URL = "http://127.0.0.1:3555/"
    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar) // Attach cookie handler
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        // Increase timeouts for AI processing (Audio transcription can be slow)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: LexityApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LexityApi::class.java)
    }
}
]]>
</file>

<file path="app/src/main/java/com/example/translate/AudioRecorder.kt">
<![CDATA[
package com.example.translate

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): File? {
        // Create a temporary file in the cache directory
        outputFile = File(context.cacheDir, "audio_record_${System.currentTimeMillis()}.m4a")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            // Settings recommended for speech recognition (16kHz, low bitrate)
            setAudioSamplingRate(16000)
            setAudioEncodingBitRate(32000)
            setOutputFile(outputFile?.absolutePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
        return outputFile
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
        }
    }
}
]]>
</file>

<file path="app/src/main/java/com/example/translate/MainActivity.kt">
<![CDATA[
package com.example.translate

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit 
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// Material Icons imports
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LexityApp()
            }
        }
    }
}

enum class AppScreen { TRANSLATOR, STUDY, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LexityApp() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Auth State
    var isLoggedIn by remember { mutableStateOf(false) }
    
    // Data State
    var userNativeLanguage by remember { mutableStateOf("english") }
    var availableLanguages by remember { mutableStateOf(LexityConstants.SUPPORTED_LANGUAGES.take(2)) }
    
    // --- Profile & Nav State ---
    var userProfile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var currentScreen by remember { mutableStateOf(AppScreen.TRANSLATOR) }

    fun fetchUserProfile() {
        scope.launch {
            try {
                val profile = RetrofitClient.api.getUserProfile()
                userProfile = profile // Save full profile
                
                // Identify User's Languages (Native + Targets)
                val userLangKeys = mutableSetOf<String>()
                
                profile.nativeLanguage?.let { 
                    userLangKeys.add(it.lowercase()) 
                    userNativeLanguage = it.lowercase()
                }
                
                profile.languageProfiles.forEach { 
                    userLangKeys.add(it.language.lowercase()) 
                }

                // Filter the Master List to only show User's Languages
                val filtered = LexityConstants.SUPPORTED_LANGUAGES.filter { 
                    userLangKeys.contains(it.value.lowercase()) 
                }

                if (filtered.isNotEmpty()) {
                    availableLanguages = filtered
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Could not load language profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { 
            isLoggedIn = true
            fetchUserProfile() // Fetch profile after successful login
        })
    } else {
        Scaffold(
            topBar = {
                // Only show TopBar on Translator/Study screens
                if (currentScreen != AppScreen.SETTINGS) {
                    TopAppBar(
                        title = { Text("Lexity") },
                        actions = {
                            IconButton(onClick = { currentScreen = AppScreen.SETTINGS }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                // Don't show bottom bar on Settings
                if (currentScreen != AppScreen.SETTINGS) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Translate, contentDescription = null) },
                            label = { Text("Translator") },
                            selected = currentScreen == AppScreen.TRANSLATOR,
                            onClick = { currentScreen = AppScreen.TRANSLATOR }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Book, contentDescription = null) }, 
                            label = { Text("Study") },
                            selected = currentScreen == AppScreen.STUDY,
                            onClick = { currentScreen = AppScreen.STUDY }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentScreen) {
                    AppScreen.TRANSLATOR -> {
                        TranslatorScreen(
                            onLogout = { isLoggedIn = false },
                            isBubbleMode = false,
                            availableLanguages = availableLanguages,
                            nativeLanguageCode = userNativeLanguage,
                            // Pass the current default from profile to set initial target state
                            initialTargetLanguageCode = userProfile?.defaultTargetLanguage
                        )
                    }
                    AppScreen.STUDY -> {
                        // Pass default target language or fallback to first available
                        val studyLang = userProfile?.defaultTargetLanguage ?: "spanish"
                        StudyScreen(targetLanguageCode = studyLang)
                    }
                    AppScreen.SETTINGS -> {
                        if (userProfile != null) {
                            SettingsScreen(
                                userProfile = userProfile!!,
                                onProfileUpdated = { updated -> 
                                    userProfile = updated 
                                },
                                onLogout = { isLoggedIn = false },
                                onBack = { currentScreen = AppScreen.TRANSLATOR }
                            )
                        } else {
                            // Fallback if profile didn't load for some reason
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                                LaunchedEffect(Unit) { fetchUserProfile() } // Try fetching again
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun performLogin() {
        focusManager.clearFocus()
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.login(LoginRequest(email.trim(), password))
                if (response.isSuccessful) {
                    Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Login Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Lexity Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                autoCorrect = false
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                autoCorrect = false
            ),
            keyboardActions = KeyboardActions(
                onDone = { performLogin() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { performLogin() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }
    }
}

@Composable
fun TranslatorScreen(
    onLogout: () -> Unit,
    isBubbleMode: Boolean = false,
    availableLanguages: List<LanguageOption>,
    nativeLanguageCode: String,
    initialTargetLanguageCode: String? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    
    // Audio Recording
    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    
    // Permission launcher for Recording
    val recordAudioLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, start recording
            recordedFile = audioRecorder.startRecording()
            if (recordedFile != null) {
                isRecording = true
                Toast.makeText(context, "Recording...", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            BubbleController.showBubble(context)
            if (!isBubbleMode) activity?.moveTaskToBack(true) // Minimize main app
        } else {
            Toast.makeText(context, "Notifications needed for Bubble mode", Toast.LENGTH_SHORT).show()
        }
    }

    // State - Initialize selection
    var sourceLang by remember { 
        mutableStateOf(availableLanguages.find { it.value == nativeLanguageCode } ?: availableLanguages.first()) 
    }
    
    // Initialize targetLang respecting the profile preference (initialTargetLanguageCode)
    var targetLang by remember(initialTargetLanguageCode, availableLanguages) { 
        mutableStateOf(
            availableLanguages.find { it.value == initialTargetLanguageCode } 
            ?: availableLanguages.find { it.value != nativeLanguageCode } 
            ?: availableLanguages.last()
        ) 
    }
    
    var inputText by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<TranslateResponse?>(null) }

    // Existing Deck State
    var existingDeck by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Fetch Deck Effect
    LaunchedEffect(targetLang) {
        try {
            val deck = RetrofitClient.api.getStudyDeck(
                targetLanguage = targetLang.value, 
                includeAll = true
            )
            existingDeck = deck.map { it.frontContent }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Function to handle Audio Translation
    fun processAudioTranslation(file: File) {
        scope.launch {
            isLoading = true
            result = null
            try {
                // Prepare Multipart Body
                val requestFile = file.asRequestBody("audio/m4a".toMediaTypeOrNull())
                val audioPart = MultipartBody.Part.createFormData("audio", file.name, requestFile)
                val sourceLangPart = sourceLang.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val targetLangPart = targetLang.name.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.api.audioTranslate(
                    audio = audioPart,
                    sourceLanguage = sourceLangPart,
                    targetLanguage = targetLangPart
                )

                // Update UI with transcribed text
                inputText = response.sourceText
                
                // Map Audio Response to TranslateResponse for common display logic
                result = TranslateResponse(
                    fullTranslation = response.translatedText,
                    segments = response.segments
                )
            } catch (e: HttpException) {
                e.printStackTrace()
                Toast.makeText(context, "Upload Failed: ${e.code()}", Toast.LENGTH_SHORT).show()
                if (e.code() == 429) {
                    Toast.makeText(context, "Daily limit reached.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
                // Clean up file
                try { file.delete() } catch (e: Exception) {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Translator",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // MINIMIZE / CLOSE BUTTON
                if (!isBubbleMode) {
                    IconButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            BubbleController.showBubble(context)
                            activity?.moveTaskToBack(true)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Minimize to Bubble",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    // If inside bubble, show an exit button
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close Bubble")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Language Selector
        LanguageSelector(
            sourceLang = sourceLang,
            targetLang = targetLang,
            availableOptions = availableLanguages,
            onSourceChange = { sourceLang = it },
            onTargetChange = { targetLang = it },
            onSwap = {
                val temp = sourceLang
                sourceLang = targetLang
                targetLang = temp
                result = null 
                inputText = ""
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Input Area
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text or record audio") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 6,
            trailingIcon = {
                // Microphone Button inside the text field
                IconButton(
                    onClick = {
                        if (isRecording) {
                            // Stop Recording
                            audioRecorder.stopRecording()
                            isRecording = false
                            recordedFile?.let { file ->
                                processAudioTranslation(file)
                            }
                        } else {
                            // Start Recording
                            recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    colors = if (isRecording) 
                        IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error) 
                    else IconButtonDefaults.iconButtonColors()
                ) {
                    if (isRecording) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop Recording")
                    } else {
                        Icon(Icons.Default.Mic, contentDescription = "Record Audio")
                    }
                }
            }
        )

        // Text Tools Row (Paste / Clear)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                clipboardManager.getText()?.text?.let { inputText = it }
            }) {
                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Paste")
            }

            if (inputText.isNotEmpty()) {
                TextButton(
                    onClick = { inputText = "" },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Action Button (Text)
        Button(
            onClick = {
                focusManager.clearFocus() 
                scope.launch {
                    isLoading = true
                    result = null 
                    try {
                        val response = RetrofitClient.api.translate(
                            TranslateRequest(
                                text = inputText,
                                sourceLanguage = sourceLang.name,
                                targetLanguage = targetLang.name
                            )
                        )
                        result = response
                    } catch (e: HttpException) {
                        e.printStackTrace()
                        if (e.code() == 401) {
                            Toast.makeText(context, "Session expired, please login again", Toast.LENGTH_LONG).show()
                            onLogout()
                        } else {
                            Toast.makeText(context, "Error: ${e.message()}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = inputText.isNotBlank() && !isLoading && !isRecording
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processing...")
            } else {
                Text("Translate Text")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Output Area (Full Translation + Segments)
        if (result != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Full Translation Header
                item {
                    Text("Full Translation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            text = result!!.fullTranslation,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Segments Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sentence Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(result!!.segments) { segment ->
                    val isLearningTarget = targetLang.value != nativeLanguageCode
                    val deckLanguage = if (isLearningTarget) targetLang.value else sourceLang.value
                    val frontText = if (isLearningTarget) segment.translation else segment.source
                    val backText = if (isLearningTarget) segment.source else segment.translation

                    val isAlreadyInDeck = existingDeck.contains(frontText)

                    SegmentCard(
                        segment = segment,
                        isAlreadyAdded = isAlreadyInDeck,
                        onAddToDeck = {
                            scope.launch {
                                try {
                                    RetrofitClient.api.createFlashcard(
                                        FlashcardRequest(
                                            frontContent = frontText,
                                            backContent = backText,
                                            targetLanguage = deckLanguage,
                                            explanation = segment.explanation
                                        )
                                    )
                                    existingDeck = existingDeck + frontText 
                                    Toast.makeText(context, "Saved to $deckLanguage deck", Toast.LENGTH_SHORT).show()
                                } catch (e: HttpException) {
                                    if (e.code() == 401) {
                                        Toast.makeText(context, "Session expired", Toast.LENGTH_SHORT).show()
                                        onLogout()
                                    } else {
                                        Toast.makeText(context, "Failed to save: ${e.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
]]>
</file>

<file path="TASKS.md">
<![CDATA[
- [x] Check if flashcard already exists in study deck
- [x] Allow user to switch default target language in Settings
- [x] Implement Study Flashcards Tab with spaced-repetition logic
- [x] Update Flashcard UI to Anki-style (Show Answer below question)
- [x] Move Logout button to Settings screen
- [x] Add Paste and Clear buttons to Translator input
- [x] Implement Audio Translation (Record, Upload, Transcribe)
]]>
</file>
</modifications>
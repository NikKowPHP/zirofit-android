package com.ziro.fit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
            is AuthState.Authenticated -> DashboardScreen(onLogout = authViewModel::logout)
            is AuthState.Error -> LoginScreen(onLogin = authViewModel::login, error = state.message)
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
fun DashboardScreen(
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
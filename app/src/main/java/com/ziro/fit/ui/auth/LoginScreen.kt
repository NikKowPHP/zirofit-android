package com.ziro.fit.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.ziro.fit.ui.components.ZiroTextField
import com.ziro.fit.ui.components.ZiroPrimaryButton
import com.ziro.fit.ui.components.ZiroSocialButton
import androidx.compose.foundation.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.ziro.fit.ui.theme.ZiroAccent

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onClearError: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    successMessage: String? = null,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val showBanner = (error != null) || (validationError != null) || (successMessage != null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "ZIRO.FIT",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZiroAccent
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Welcome Back", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground))
            Text("Sign in to continue", style = TextStyle(fontSize = 14.sp, color = Color.Gray))
            Spacer(modifier = Modifier.height(32.dp))
            ZiroTextField(
                value = email,
                onValueChange = { email = it; validationError = null },
                placeholder = "Email",
                icon = { Icon(imageVector = Icons.Filled.Email, contentDescription = null, tint = Color.Gray) },
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(12.dp))
            ZiroTextField(
                value = password,
                onValueChange = { password = it; validationError = null },
                placeholder = "Password",
                icon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = Color.Gray) },
                isPassword = true,
                keyboardType = KeyboardType.Password,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text("Forgot Password?", color = ZiroAccent, fontSize = 14.sp, fontWeight = FontWeight.Normal)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            ZiroPrimaryButton(
                text = "Sign In",
                onClick = {
                    onClearError()
                    if (email.isBlank() || password.isBlank()) {
                        validationError = "Please enter email and password"
                    } else {
                        onLogin(email, password)
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Divider(Modifier.weight(1f))
                Text("or", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                Divider(Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            ZiroSocialButton(
                text = "Continue with Google",
                onClick = onGoogleSignIn,
                icon = { Text("G", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ZiroSocialButton(
                text = "Continue with Apple",
                onClick = onAppleSignIn,
                icon = { Text("\u2318", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Don't have an account?", fontSize = 14.sp)
                TextButton(onClick = { }) {
                    Text("Sign Up", color = ZiroAccent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
        AnimatedVisibility(visible = showBanner, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(text = error ?: successMessage ?: "", color = if (error != null) Color.Red else Color.Green, fontSize = 14.sp)
            }
        }
    }
}

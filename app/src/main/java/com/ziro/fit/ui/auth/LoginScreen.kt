package com.ziro.fit.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.ziro.fit.ui.components.ZiroTextField
import com.ziro.fit.ui.components.ZiroPrimaryButton
import com.ziro.fit.ui.components.ZiroSocialButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.CheckCircle
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
    val darkTheme = isSystemInDarkTheme()

    val showBanner = (error != null) || (validationError != null) || (successMessage != null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))
            
            // Premium Brand Header
            Text(
                text = "ZIRO.FIT",
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ZiroAccent,
                    letterSpacing = 1.sp
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Welcome Back",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Sign in to continue",
                style = TextStyle(
                    fontSize = 15.sp,
                    color = if (darkTheme) Color.Gray else Color.Gray.copy(alpha = 0.8f)
                )
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            // Premium Notification Banner
            AnimatedVisibility(
                visible = showBanner,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (error != null || validationError != null) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (error != null || validationError != null) {
                                Icons.Default.ErrorOutline
                            } else {
                                Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = if (error != null || validationError != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error ?: validationError ?: successMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (error != null || validationError != null) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Input Fields
            ZiroTextField(
                value = email,
                onValueChange = { 
                    email = it
                    validationError = null 
                },
                placeholder = "Email",
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email",
                        tint = if (darkTheme) Color.Gray else Color.Gray.copy(alpha = 0.6f)
                    ) 
                },
                keyboardType = KeyboardType.Email
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ZiroTextField(
                value = password,
                onValueChange = { 
                    password = it
                    validationError = null 
                },
                placeholder = "Password",
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password",
                        tint = if (darkTheme) Color.Gray else Color.Gray.copy(alpha = 0.6f)
                    ) 
                },
                isPassword = true,
                keyboardType = KeyboardType.Password,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible }
            )
            
            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onNavigateToForgotPassword,
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = ZiroAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sign In Action Button
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Or Divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = if (darkTheme) Color.DarkGray.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.5f)
                )
                Text(
                    text = "or",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = if (darkTheme) Color.DarkGray.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Social Single Sign-On Actions
            ZiroSocialButton(
                text = "Continue with Google",
                onClick = onGoogleSignIn,
                icon = { 
                    Text(
                        text = "G",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (darkTheme) Color.White else Color.Black
                    ) 
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ZiroSocialButton(
                text = "Continue with Apple",
                onClick = onAppleSignIn,
                icon = { 
                    Text(
                        text = "",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (darkTheme) Color.White else Color.Black
                    ) 
                }
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Bottom Action Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        color = ZiroAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
      
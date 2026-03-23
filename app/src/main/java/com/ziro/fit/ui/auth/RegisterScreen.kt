package com.ziro.fit.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.ui.components.ZiroPrimaryButton
import com.ziro.fit.ui.components.ZiroTextField
import com.ziro.fit.ui.theme.ZiroAccent

@Composable
fun RegisterScreen(
    onRegister: (String, String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(name, email, password, confirmPassword) {
        if (validationError != null) validationError = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "ZIRO.FIT",
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ZiroAccent)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Create an account",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        )
        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(visible = error != null || validationError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = error ?: validationError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ZiroTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "Full Name",
            icon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(12.dp))
        ZiroTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email",
            icon = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(12.dp))
        ZiroTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            icon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
            isPassword = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        ZiroTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Confirm Password",
            icon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        ZiroPrimaryButton(
            text = "Create Account",
            onClick = {
                onClearError()
                when {
                    name.isBlank() || email.isBlank() || password.isBlank() -> validationError = "Please fill in all fields"
                    password != confirmPassword -> validationError = "Passwords do not match"
                    password.length < 8 -> validationError = "Password must be at least 8 characters"
                    else -> onRegister(name, email, password)
                }
            },
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onNavigateToLogin) {
                Text("Sign In", fontWeight = FontWeight.SemiBold, color = ZiroAccent)
            }
        }
    }
}

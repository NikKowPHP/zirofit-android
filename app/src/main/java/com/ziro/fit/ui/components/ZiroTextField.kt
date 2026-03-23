package com.ziro.fit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.ui.theme.ZiroAccent
import com.ziro.fit.ui.theme.ZiroInputBackground
import com.ziro.fit.ui.theme.ZiroInputBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun ZiroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: @Composable () -> Unit,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Email,
    modifier: Modifier = Modifier,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) ZiroAccent else ZiroInputBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ZiroInputBackground, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .border(width = 1.5.dp, color = borderColor, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp),
                    visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(text = placeholder, color = Color.Gray, fontSize = 16.sp)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (isPassword && onPasswordToggle != null) {
                IconButton(onClick = { onPasswordToggle() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

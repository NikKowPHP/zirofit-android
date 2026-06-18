package com.ziro.fit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PremiumDarkColorScheme = darkColorScheme(
    primary = ZiroAccent,
    onPrimary = Color.White,
    primaryContainer = ZiroAccent.copy(alpha = 0.15f),
    onPrimaryContainer = Color.White,
    secondary = StrongTextSecondary,
    onSecondary = Color.White,
    tertiary = ExplorePurple,
    onTertiary = Color.White,
    background = StrongBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = StrongSurface,
    onSurface = Color.White,
    surfaceVariant = StrongSecondaryBackground,
    onSurfaceVariant = StrongTextSecondary,
    outline = StrongDivider,
    error = StrongRed,
    errorContainer = StrongRed.copy(alpha = 0.15f),
    onErrorContainer = Color.White
)

private val PremiumLightColorScheme = lightColorScheme(
    primary = ZiroAccent,
    onPrimary = Color.White,
    primaryContainer = ZiroAccent.copy(alpha = 0.1f),
    onPrimaryContainer = ZiroAccent,
    secondary = Color(0xFF475569),
    onSecondary = Color(0xFF0F172A),
    tertiary = ExplorePurple,
    onTertiary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFF8FAFC),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0),
    error = StrongRed,
    errorContainer = StrongRed.copy(alpha = 0.1f),
    onErrorContainer = StrongRed
)

@Composable
fun ZirofitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set default dynamicColor to false to maintain strict premium brand palette consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> PremiumDarkColorScheme
        else -> PremiumLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
      
package com.ziro.fit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.ziro.fit.ui.theme.ZiroSocialButtonBg
import com.ziro.fit.ui.theme.ZiroSocialButtonBgDark
import com.ziro.fit.ui.theme.ZiroSocialButtonStroke
import com.ziro.fit.ui.theme.ZiroSocialButtonStrokeDark
import com.ziro.fit.ui.theme.ZiroSocialButtonText
import com.ziro.fit.ui.theme.ZiroSocialButtonTextDark

@Composable
fun ZiroSocialButton(
    text: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme = isSystemInDarkTheme()
    val bg = if (darkTheme) ZiroSocialButtonBgDark else ZiroSocialButtonBg
    val stroke = if (darkTheme) ZiroSocialButtonStrokeDark else ZiroSocialButtonStroke
    val textColor = if (darkTheme) ZiroSocialButtonTextDark else ZiroSocialButtonText

    Surface(
        color = bg,
        shape = RoundedCornerShape(100.dp), // Premium capsule shape
        border = BorderStroke(1.2.dp, stroke),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = textColor.copy(alpha = 0.1f)),
                    onClick = onClick
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
      
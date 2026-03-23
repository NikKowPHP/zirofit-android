package com.ziro.fit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.ziro.fit.ui.theme.ZiroSocialButtonBg
import com.ziro.fit.ui.theme.ZiroSocialButtonStroke

@Composable
fun ZiroSocialButton(
    text: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = ZiroSocialButtonBg,
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, ZiroSocialButtonStroke),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(12.dp))
                Text(text, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

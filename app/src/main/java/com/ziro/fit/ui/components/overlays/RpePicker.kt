package com.ziro.fit.ui.components.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RpeOption(
    val value: Double,
    val description: String,
    val repsLeft: Int
)

val rpeOptions = listOf(
    RpeOption(10.0, "Maximum Effort", 0),
    RpeOption(9.5, "1 rep left", 1),
    RpeOption(9.0, "1-2 reps left", 1),
    RpeOption(8.5, "2 reps left", 2),
    RpeOption(8.0, "2-3 reps left", 2),
    RpeOption(7.5, "3 reps left", 3),
    RpeOption(7.0, "3-4 reps left", 3),
    RpeOption(6.5, "4 reps left", 4),
    RpeOption(6.0, "4-5 reps left", 4),
    RpeOption(5.5, "5 reps left", 5),
    RpeOption(5.0, "5-6 reps left", 5)
)

@Composable
fun RpePicker(
    selectedRpe: Double?,
    onRpeSelected: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Rate of Perceived Exertion (RPE)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(rpeOptions) { option ->
                RpeCard(
                    option = option,
                    isSelected = selectedRpe == option.value,
                    onClick = { onRpeSelected(option.value) }
                )
            }
        }

        if (selectedRpe != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "RPE ${selectedRpe}: ${rpeOptions.find { it.value == selectedRpe }?.description ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RpeCard(
    option: RpeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        getRpeColor(option.value)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    getRpeColor(option.value),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = option.value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${option.repsLeft} rep${if (option.repsLeft != 1) "s" else ""} left",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun getRpeColor(rpe: Double): Color {
    return when {
        rpe >= 9.5 -> Color(0xFFE53935)
        rpe >= 8.5 -> Color(0xFFFF7043)
        rpe >= 7.5 -> Color(0xFFFF9800)
        rpe >= 6.5 -> Color(0xFFFFCA28)
        rpe >= 5.5 -> Color(0xFF66BB6A)
        else -> Color(0xFF42A5F5)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpePickerBottomSheet(
    selectedRpe: Double?,
    onRpeSelected: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Select RPE",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            RpePicker(
                selectedRpe = selectedRpe,
                onRpeSelected = { rpe ->
                    onRpeSelected(rpe)
                    onDismiss()
                }
            )
        }
    }
}

package com.ziro.fit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.model.Exercise
import com.ziro.fit.ui.theme.*

@Composable
fun ExerciseBrowserContent(
    exercises: List<Exercise>,
    isLoading: Boolean,
    onSearch: (String) -> Unit,
    onAddExercises: (List<Exercise>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<Exercise>() }
    
    Column(modifier = Modifier
        .fillMaxSize()
        .background(StrongBackground)
        .padding(16.dp)) {
            
        Text("Add Exercises", style = MaterialTheme.typography.headlineSmall, color = StrongTextPrimary)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                onSearch(it) 
            },
            placeholder = { Text("Search Exercises", color = StrongTextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = StrongTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = StrongTextPrimary,
                unfocusedTextColor = StrongTextPrimary,
                focusedContainerColor = StrongInputBackground,
                unfocusedContainerColor = StrongInputBackground,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp)
        )
        
        Spacer(Modifier.height(16.dp))
        
        if (isLoading) {
            Box(Modifier
                .fillMaxWidth()
                .weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StrongBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exercises) { exercise ->
                    val isSelected = selectedExercises.any { it.id == exercise.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) {
                                    selectedExercises.removeAll { it.id == exercise.id }
                                } else {
                                    selectedExercises.add(exercise)
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar/Image placeholder
                         Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(StrongSecondaryBackground),
                            contentAlignment = Alignment.Center
                         ) {
                             Text(exercise.name.take(1), color = StrongTextSecondary)
                         }
                         
                         Spacer(Modifier.width(16.dp))
                         
                         Column(modifier = Modifier.weight(1f)) {
                             Text(exercise.name, color = StrongTextPrimary, fontWeight = FontWeight.SemiBold)
                             Text(exercise.muscleGroup ?: "Cardio", color = StrongTextSecondary, fontSize = 12.sp)
                         }
                         
                         if (isSelected) {
                             Icon(Icons.Default.Check, null, tint = StrongBlue)
                         }
                    }
                    HorizontalDivider(color = StrongDivider)
                }
            }
            
            // Add Button Footer
            if (selectedExercises.isNotEmpty()) {
                Button(
                    onClick = { onAddExercises(selectedExercises.toList()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)
                ) {
                    Text("Add (${selectedExercises.size})")
                }
            }
        }
    }
}

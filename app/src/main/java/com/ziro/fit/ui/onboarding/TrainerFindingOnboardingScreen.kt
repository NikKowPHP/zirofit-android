package com.ziro.fit.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerFindingOnboardingScreen(
    onFinish: (String?, String?) -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    var locationPreference by remember { mutableStateOf("In-Person") }
    var city by remember { mutableStateOf("") }
    
    val totalSteps = 4
    
    val goals = listOf(
        "Lose Weight" to Icons.Default.LocalFireDepartment,
        "Build Muscle" to Icons.Default.FitnessCenter,
        "Improve Mobility" to Icons.Default.Accessibility,
        "Learn Skills" to Icons.Default.SportsMma,
        "CrossFit" to Icons.Default.FitnessCenter,
        "Yoga & Pilates" to Icons.Default.SelfImprovement,
        "Performance" to Icons.Default.Bolt
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Find Your Coach") })
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.Transparent) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    if (currentStep > 0) {
                        TextButton(onClick = { currentStep -= 1 }) { Text("Back", color = Color.Gray) }
                    } else {
                        TextButton(onClick = onSkip) { Text("Skip", color = Color.Gray) }
                    }
                    
                    Button(
                        onClick = { 
                            if (currentStep < totalSteps - 1) currentStep += 1 
                            else {
                                val spec = selectedGoal
                                val loc = if (locationPreference == "In-Person") city else null
                                onFinish(spec, loc)
                            }
                        },
                        enabled = (currentStep != 1 || selectedGoal != null)
                    ) {
                        Text(if (currentStep == totalSteps - 1) "Show Matches" else "Next")
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0 until totalSteps) {
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(if (i <= currentStep) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha=0.3f), RoundedCornerShape(2.dp)))
                }
            }
            
            AnimatedContent(targetState = currentStep, label = "onboarding_steps") { step ->
                when(step) {
                    0 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Spacer(Modifier.height(40.dp))
                            Box(modifier = Modifier.size(180.dp).background(MaterialTheme.colorScheme.primary.copy(alpha=0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(30.dp))
                            Text("Let's Find Your Coach", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(30.dp))
                            Text("We'll ask a few quick questions to match you with the perfect expert for your goals.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                    1 -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("What's your main goal?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(24.dp))
                            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(goals) { (goal, icon) ->
                                    Card(
                                        modifier = Modifier.height(120.dp).clickable { selectedGoal = goal },
                                        colors = CardDefaults.cardColors(containerColor = if (selectedGoal == goal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                        border = if (selectedGoal == goal) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.3f))
                                    ) {
                                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = if (selectedGoal == goal) Color.White else MaterialTheme.colorScheme.primary)
                                            Spacer(Modifier.height(12.dp))
                                            Text(goal, color = if (selectedGoal == goal) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("How do you want to train?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(24.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                listOf("In-Person" to Icons.Default.Place, "Online" to Icons.Default.Wifi).forEach { (pref, icon) ->
                                    Card(
                                        modifier = Modifier.weight(1f).height(100.dp).clickable { locationPreference = pref },
                                        colors = CardDefaults.cardColors(containerColor = if (locationPreference == pref) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Icon(icon, contentDescription = null, modifier = Modifier.size(30.dp), tint = if (locationPreference == pref) Color.White else MaterialTheme.colorScheme.primary)
                                            Spacer(Modifier.height(8.dp))
                                            Text(pref, color = if (locationPreference == pref) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            
                            if (locationPreference == "In-Person") {
                                Spacer(Modifier.height(24.dp))
                                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Enter City (e.g. New York)") }, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                    3 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Spacer(Modifier.height(40.dp))
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color(0xFFFFD700))
                            Spacer(Modifier.height(30.dp))
                            Text("We found valid matches!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(30.dp))
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Based on your goal:", color = Color.Gray)
                                    Text(selectedGoal ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    if (locationPreference == "In-Person" && city.isNotBlank()) {
                                        Spacer(Modifier.height(8.dp))
                                        Text("In:", color = Color.Gray)
                                        Text(city, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

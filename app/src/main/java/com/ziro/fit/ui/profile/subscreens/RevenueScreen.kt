package com.ziro.fit.ui.profile.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueScreen(
    onNavigateBack: () -> Unit
) {
    val totalEarnings = "$12,450.00"
    val availableForPayout = "$1,200.00"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revenue") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main Balance
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Available for Payout", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Text(availableForPayout, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { /* Handle withdraw */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Withdraw Now")
                    }
                }
            }

            // Stats Grid
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                RevenueStatCard(title = "Lifetime", value = totalEarnings, modifier = Modifier.weight(1f))
                RevenueStatCard(title = "Last Payout", value = "$3,400", modifier = Modifier.weight(1f))
            }

            // Transactions
            Text("Recent Transactions", style = MaterialTheme.typography.titleLarge)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TransactionRow("Program Sale: Leg Day Pro", "Today", "+$49.99")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    TransactionRow("Personal Session: John Doe", "Yesterday", "+$75.00")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    TransactionRow("Platform Fee (5%)", "Yesterday", "-$6.25", isNegative = true)
                }
            }
        }
    }
}

@Composable
fun RevenueStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun TransactionRow(title: String, date: String, amount: String, isNegative: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isNegative) Color.Red else Color(0xFF4CAF50)
        )
    }
}

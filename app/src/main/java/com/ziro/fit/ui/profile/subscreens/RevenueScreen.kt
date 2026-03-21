package com.ziro.fit.ui.profile.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziro.fit.viewmodel.RevenueUiState
import com.ziro.fit.viewmodel.RevenueViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueScreen(
    onNavigateBack: () -> Unit,
    viewModel: RevenueViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRevenue()
    }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadRevenue() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    RevenueContent(uiState = uiState)
                }
            }
        }
    }
}

@Composable
private fun RevenueContent(uiState: RevenueUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Available for Payout", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                Text(
                    text = uiState.availableForPayout,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    Text("Withdraw Now")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            RevenueStatCard(
                title = "Lifetime",
                value = uiState.totalEarnings,
                modifier = Modifier.weight(1f)
            )
            RevenueStatCard(
                title = "Last Payout",
                value = "$3,400",
                modifier = Modifier.weight(1f)
            )
        }

        Text("Recent Transactions", style = MaterialTheme.typography.titleLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val transactions = uiState.revenueData?.transactions ?: emptyList()
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No transactions yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(transactions) { transaction ->
                        TransactionRow(
                            title = transaction.title,
                            date = transaction.date,
                            amount = formatTransactionAmount(transaction.amount),
                            isNegative = transaction.type == "fee"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

private fun formatTransactionAmount(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    val formatted = format.format(kotlin.math.abs(amount))
    return if (amount < 0) "-$formatted" else "+$formatted"
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

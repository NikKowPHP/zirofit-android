package com.ziro.fit.ui.profile.subscreens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.ArrowBack
 import androidx.compose.material.icons.filled.CheckCircle
 import androidx.compose.material.icons.filled.CreditCard
 import androidx.compose.material.icons.filled.AccountBalance
 import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.viewmodel.PayoutsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutsScreen(
    onNavigateBack: () -> Unit,
    shouldRefresh: Boolean = false,
    viewModel: PayoutsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Refresh Stripe status when returning from Stripe onboarding
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.fetchStripeStatus()
        }
    }

    LaunchedEffect(uiState.onboardingUrl) {
        uiState.onboardingUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            viewModel.clearOnboardingUrl()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payouts") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Payments & Payouts", style = MaterialTheme.typography.headlineSmall)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Stripe Connect", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                            val isConnected = uiState.billingStatus?.chargesEnabled == true
                            Text(
                                if (isConnected) "Connected & Ready" else "Setup Incomplete",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFFF9800)
                            )
                        }
                        if (uiState.billingStatus?.chargesEnabled == true) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                        }
                    }

                    Text(
                        "Connect your bank account via Stripe to receive payments for your packages and services directly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Button(
                        onClick = { viewModel.fetchStripeOnboardingUrl() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                     ) {
                         if (uiState.isLoading && uiState.onboardingUrl == null) {
                             CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                         } else {
                             Text(if (uiState.billingStatus?.detailsSubmitted == true) "Manage Stripe Account" else "Connect Stripe Account")
                     }
                 }
             }
 
             Card(
                 colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                 modifier = Modifier.fillMaxWidth()
              ) {
                  Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                      val isPayoutsEnabled = uiState.billingStatus?.payoutsEnabled == true
                      Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                          Spacer(modifier = Modifier.width(16.dp))
                          Column(modifier = Modifier.weight(1f)) {
                              Text("Payouts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
  
                              Text(
                                  if (isPayoutsEnabled) "Ready to Receive Payouts" else "Payouts Not Available",
                                  style = MaterialTheme.typography.bodyMedium,
                                  color = if (isPayoutsEnabled) Color(0xFF4CAF50) else Color(0xFFFF9800)
                              )
                          }
                          if (isPayoutsEnabled) {
                              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                          }
                      }
  
                      Text(
                          if (isPayoutsEnabled) {
                              "Your payouts are enabled. Funds will be transferred to your linked bank account."
                          } else {
                              "Payouts require a completed Stripe Connect account. Complete the setup to receive your earnings."
                          },
                          style = MaterialTheme.typography.bodySmall,
                          color = Color.Gray
                      )
                   }
               }
          }
      }
  }
}

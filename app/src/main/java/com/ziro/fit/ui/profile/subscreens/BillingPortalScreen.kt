package com.ziro.fit.ui.profile.subscreens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.SubscriptionInfo
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.BillingPortalViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingPortalScreen(
    onNavigateBack: () -> Unit,
    viewModel: BillingPortalViewModel = hiltViewModel()
) {
    val subscription = viewModel.subscription
    val isLoading = viewModel.isLoading
    val isLoadingPortal = viewModel.isLoadingPortal
    val error = viewModel.error
    val portalUrl = viewModel.portalUrl

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadSubscription()
    }

    LaunchedEffect(portalUrl) {
        portalUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            viewModel.clearPortalUrl()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billing", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrongBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = StrongBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && subscription == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = StrongBlue
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    subscription?.let { sub ->
                        SubscriptionCard(subscription = sub)
                    } ?: run {
                        EmptySubscriptionCard()
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.openBillingPortal() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoadingPortal,
                        colors = ButtonDefaults.buttonColors(containerColor = StrongBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoadingPortal) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.CreditCard, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Manage Billing", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = StrongSecondaryBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Billing Portal",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Access your billing portal to manage your subscription, update payment methods, view invoices, and more.",
                                color = StrongTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionCard(subscription: SubscriptionInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = StrongSecondaryBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Subscription",
                    color = StrongTextSecondary,
                    fontSize = 14.sp
                )
                StatusBadge(status = subscription.subscriptionStatus ?: "unknown")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = subscription.tierName ?: subscription.tier ?: "Free Plan",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = StrongSurface)

            Spacer(modifier = Modifier.height(16.dp))

            if (subscription.freeMode == true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StrongGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Free mode active",
                        color = StrongGreen,
                        fontSize = 14.sp
                    )
                }
            } else {
                subscription.stripeCurrentPeriodEnd?.let { endDate ->
                    val formattedDate = try {
                        val zonedDateTime = ZonedDateTime.parse(endDate)
                        zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                    } catch (e: Exception) {
                        endDate
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = StrongBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Next billing date",
                                color = StrongTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = formattedDate,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (subscription.stripeCancelAtPeriodEnd == true) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = StrongRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Subscription will cancel at period end",
                                color = StrongRed,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                subscription.trialEndsAt?.let { trialEnd ->
                    Spacer(modifier = Modifier.height(12.dp))
                    val formattedDate = try {
                        val zonedDateTime = ZonedDateTime.parse(trialEnd)
                        zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                    } catch (e: Exception) {
                        trialEnd
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Science,
                            contentDescription = null,
                            tint = StrongBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Trial ends",
                                color = StrongTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = formattedDate,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "active", "trialing" -> Pair(StrongGreen.copy(alpha = 0.2f), StrongGreen)
        "past_due", "unpaid" -> Pair(StrongRed.copy(alpha = 0.2f), StrongRed)
        "canceled", "cancelled" -> Pair(StrongTextSecondary.copy(alpha = 0.2f), StrongTextSecondary)
        else -> Pair(StrongBlue.copy(alpha = 0.2f), StrongBlue)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() }.replace("_", " "),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptySubscriptionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = StrongSecondaryBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CreditCard,
                contentDescription = null,
                tint = StrongTextSecondary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No active subscription",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage your billing to subscribe to a plan",
                color = StrongTextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a single revenue/transaction record for a trainer.
 * @param id Unique transaction identifier
 * @param title Description/title (e.g., "Program Sale: Leg Day Pro", "Platform Fee (5%)")
 * @param date Date string (e.g., "Today", "Yesterday", or ISO date)
 * @param amount Raw amount value (e.g., 49.99)
 * @param type Type of transaction: "sale", "fee", "payout", "adjustment"
 * @param status Optional status: "completed", "pending", "failed"
 */
data class RevenueTransaction(
    val id: String,
    val title: String,
    val date: String,
    val amount: Double,
    val type: String,
    val status: String? = null
)

/**
 * Response wrapper for revenue/transactions endpoint.
 * @param totalEarnings Sum of all earnings (may be calculated)
 * @param availableForPayout Amount available for withdrawal
 * @param transactions List of recent revenue transactions
 */
data class RevenueResponse(
    val totalEarnings: Double,
    val availableForPayout: Double,
    val transactions: List<RevenueTransaction>
)

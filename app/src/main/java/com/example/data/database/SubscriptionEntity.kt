package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val userEmail: String,
    val planTier: String, // "FREE", "PREMIUM", "CORPORATE"
    val status: String, // "ACTIVE", "EXPIRED", "PENDING"
    val startDate: Long,
    val endDate: Long,
    val autoRenew: Boolean = false,
    val transactionId: String = ""
)

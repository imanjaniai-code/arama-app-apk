package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventType: String, // "LOGIN_SUCCESS", "LOGIN_FAILURE", "DATA_WIPE", "BIOMETRIC_TOGGLE", "DB_BACKUP", "DB_RESTORE", "CONSENT_UPDATE", "OTP_REQUEST", "PLAN_UPGRADE"
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userEmail: String
)

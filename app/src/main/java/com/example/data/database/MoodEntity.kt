package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moodValue: String, // Emoji or status symbol
    val moodLabel: String, // Persian label
    val note: String,      // Optional short note
    val date: String,      // yyyy-MM-dd date representation
    val timestamp: Long = System.currentTimeMillis()
)

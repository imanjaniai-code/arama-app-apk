package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_journals")
data class VoiceJournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recordedText: String,      // Speech to text transcribed content
    val summary: String,           // AI generated summary
    val moodSuggestion: String,     // AI generated mood/emotional status
    val durationSeconds: Int = 0,  // Length of the audio recorded in seconds
    val timestamp: Long = System.currentTimeMillis()
)

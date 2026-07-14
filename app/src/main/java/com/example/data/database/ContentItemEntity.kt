package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "content_items")
data class ContentItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String, // عنوان فارسی
    val category: String, // MEDITATION / BREATHING / SOUND
    val shortDescription: String, // توضیح کوتاه
    val durationSeconds: Int, // مدت‌زمان (ثانیه)
    val iconEmoji: String, // آیکون/ایموجی
    val isFree: Boolean,
    
    // Breathing cycle patterns (in seconds)
    val inhaleSeconds: Int = 4,
    val holdSeconds: Int = 4,
    val exhaleSeconds: Int = 4,
    
    // Guided text for meditations
    val guidedText: String = "",
    
    // Sound synthesis type (for SOUND category, e.g., "rain", "ocean", "white", "forest", "fire")
    val soundType: String = ""
) : Serializable

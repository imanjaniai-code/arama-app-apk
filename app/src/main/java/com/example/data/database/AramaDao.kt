package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AramaDao {
    // Mood operations
    @Query("SELECT * FROM moods ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntity)

    @Query("DELETE FROM moods")
    suspend fun deleteAllMoods()

    // Chat operations
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllChatMessages()
}

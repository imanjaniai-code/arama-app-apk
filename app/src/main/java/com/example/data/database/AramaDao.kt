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
    suspend fun insertChatMessage(message: ChatEntity): Long

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteChatMessageById(id: Int)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllChatMessages()

    // Security log operations
    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC")
    fun getAllSecurityLogs(): Flow<List<SecurityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityLog(log: SecurityLogEntity)

    @Query("DELETE FROM security_logs")
    suspend fun deleteAllSecurityLogs()

    // Subscription operations
    @Query("SELECT * FROM subscriptions WHERE userEmail = :email LIMIT 1")
    suspend fun getSubscriptionForUser(email: String): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAllSubscriptions()

    // Content Library operations
    @Query("SELECT * FROM content_items ORDER BY id ASC")
    fun getAllContentItems(): Flow<List<ContentItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentItems(items: List<ContentItemEntity>)

    @Query("SELECT COUNT(*) FROM content_items")
    suspend fun getContentItemCount(): Int

    // Voice Journal operations
    @Query("SELECT * FROM voice_journals ORDER BY timestamp DESC")
    fun getAllVoiceJournals(): Flow<List<VoiceJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceJournal(journal: VoiceJournalEntity)

    @Query("DELETE FROM voice_journals WHERE id = :id")
    suspend fun deleteVoiceJournalById(id: Int)

    @Query("DELETE FROM voice_journals")
    suspend fun deleteAllVoiceJournals()
}

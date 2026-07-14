package com.example.data.repository

import com.example.data.database.AramaDao
import com.example.data.database.ChatEntity
import com.example.data.database.MoodEntity
import com.example.data.database.SecurityLogEntity
import com.example.data.database.SubscriptionEntity
import com.example.data.database.ContentItemEntity
import com.example.data.database.VoiceJournalEntity
import kotlinx.coroutines.flow.Flow

class AramaRepository(private val aramaDao: AramaDao) {
    val allMoods: Flow<List<MoodEntity>> = aramaDao.getAllMoods()
    val allChatMessages: Flow<List<ChatEntity>> = aramaDao.getAllChatMessages()
    val allSecurityLogs: Flow<List<SecurityLogEntity>> = aramaDao.getAllSecurityLogs()
    val allContentItems: Flow<List<ContentItemEntity>> = aramaDao.getAllContentItems()
    val allVoiceJournals: Flow<List<VoiceJournalEntity>> = aramaDao.getAllVoiceJournals()

    suspend fun insertVoiceJournal(journal: VoiceJournalEntity) {
        aramaDao.insertVoiceJournal(journal)
    }

    suspend fun deleteVoiceJournalById(id: Int) {
        aramaDao.deleteVoiceJournalById(id)
    }

    suspend fun deleteAllVoiceJournals() {
        aramaDao.deleteAllVoiceJournals()
    }

    suspend fun insertMood(mood: MoodEntity) {
        aramaDao.insertMood(mood)
    }

    suspend fun insertChatMessage(message: ChatEntity): Long {
        return aramaDao.insertChatMessage(message)
    }

    suspend fun deleteChatMessageById(id: Int) {
        aramaDao.deleteChatMessageById(id)
    }

    suspend fun insertSecurityLog(log: SecurityLogEntity) {
        aramaDao.insertSecurityLog(log)
    }

    suspend fun getSubscriptionForUser(email: String): SubscriptionEntity? {
        return aramaDao.getSubscriptionForUser(email)
    }

    suspend fun insertSubscription(subscription: SubscriptionEntity) {
        aramaDao.insertSubscription(subscription)
    }

    suspend fun insertContentItems(items: List<ContentItemEntity>) {
        aramaDao.insertContentItems(items)
    }

    suspend fun getContentItemCount(): Int {
        return aramaDao.getContentItemCount()
    }

    suspend fun clearAllData() {
        aramaDao.deleteAllMoods()
        aramaDao.deleteAllChatMessages()
        aramaDao.deleteAllSecurityLogs()
        aramaDao.deleteAllSubscriptions()
        aramaDao.deleteAllVoiceJournals()
    }
}

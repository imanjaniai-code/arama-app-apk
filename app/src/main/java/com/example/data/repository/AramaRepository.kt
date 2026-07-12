package com.example.data.repository

import com.example.data.database.AramaDao
import com.example.data.database.ChatEntity
import com.example.data.database.MoodEntity
import com.example.data.database.SecurityLogEntity
import com.example.data.database.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

class AramaRepository(private val aramaDao: AramaDao) {
    val allMoods: Flow<List<MoodEntity>> = aramaDao.getAllMoods()
    val allChatMessages: Flow<List<ChatEntity>> = aramaDao.getAllChatMessages()
    val allSecurityLogs: Flow<List<SecurityLogEntity>> = aramaDao.getAllSecurityLogs()

    suspend fun insertMood(mood: MoodEntity) {
        aramaDao.insertMood(mood)
    }

    suspend fun insertChatMessage(message: ChatEntity): Long {
        return aramaDao.insertChatMessage(message)
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

    suspend fun clearAllData() {
        aramaDao.deleteAllMoods()
        aramaDao.deleteAllChatMessages()
        aramaDao.deleteAllSecurityLogs()
        aramaDao.deleteAllSubscriptions()
    }
}

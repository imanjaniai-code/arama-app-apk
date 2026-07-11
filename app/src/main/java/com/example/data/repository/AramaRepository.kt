package com.example.data.repository

import com.example.data.database.AramaDao
import com.example.data.database.ChatEntity
import com.example.data.database.MoodEntity
import kotlinx.coroutines.flow.Flow

class AramaRepository(private val aramaDao: AramaDao) {
    val allMoods: Flow<List<MoodEntity>> = aramaDao.getAllMoods()
    val allChatMessages: Flow<List<ChatEntity>> = aramaDao.getAllChatMessages()

    suspend fun insertMood(mood: MoodEntity) {
        aramaDao.insertMood(mood)
    }

    suspend fun insertChatMessage(message: ChatEntity) {
        aramaDao.insertChatMessage(message)
    }

    suspend fun clearAllData() {
        aramaDao.deleteAllMoods()
        aramaDao.deleteAllChatMessages()
    }
}

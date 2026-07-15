package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase

@Database(entities = [MoodEntity::class, ChatEntity::class, SecurityLogEntity::class, SubscriptionEntity::class, ContentItemEntity::class, VoiceJournalEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aramaDao(): AramaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    // Initialize SQLCipher libraries
                    SQLiteDatabase.loadLibs(context)

                    // Retrieve or generate secure database passphrase
                    val passphrase = DatabaseKeyManager.getPassphrase(context)
                    
                    // Verify/recover legacy databases that might be unencrypted or corrupted
                    val dbFile = context.getDatabasePath("arama_database")
                    if (dbFile.exists()) {
                        try {
                            val db = SQLiteDatabase.openOrCreateDatabase(
                                dbFile.absolutePath,
                                passphrase,
                                null
                            )
                            db.close()
                        } catch (e: Throwable) {
                            android.util.Log.w("AppDatabase", "Database verification failed (likely plain-text legacy database or native library mismatch). Deleting to recreate securely.", e)
                            try {
                                context.deleteDatabase("arama_database")
                            } catch (de: Exception) {
                                android.util.Log.w("AppDatabase", "Failed to delete corrupted database", de)
                            }
                        }
                    }

                    val factory = SupportFactory(passphrase)

                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "arama_database"
                    )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    INSTANCE = instance
                    instance
                } catch (t: Throwable) {
                    android.util.Log.e("AppDatabase", "SQLCipher or secure database initialization failed. Falling back to standard unencrypted database.", t)
                    
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "arama_database_unencrypted"
                    )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
}

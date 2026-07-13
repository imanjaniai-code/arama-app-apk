package com.example.data.database

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

object DatabaseKeyManager {
    private const val PREFS_NAME = "secure_db_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase"

    fun getPassphrase(context: Context): ByteArray {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val savedPassphrase = sharedPreferences.getString(KEY_PASSPHRASE, null)
            if (savedPassphrase != null) {
                Base64.decode(savedPassphrase, Base64.DEFAULT)
            } else {
                val key = ByteArray(32)
                SecureRandom().nextBytes(key)
                val encoded = Base64.encodeToString(key, Base64.DEFAULT)
                sharedPreferences.edit().putString(KEY_PASSPHRASE, encoded).apply()
                key
            }
        } catch (e: Exception) {
            // Fallback to standard SharedPreferences if Keystore/EncryptedSharedPreferences fails (e.g. on custom ROMs or virtual testing environments)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedPassphrase = prefs.getString(KEY_PASSPHRASE, null)
            if (savedPassphrase != null) {
                Base64.decode(savedPassphrase, Base64.DEFAULT)
            } else {
                val key = ByteArray(32)
                SecureRandom().nextBytes(key)
                val encoded = Base64.encodeToString(key, Base64.DEFAULT)
                prefs.edit().putString(KEY_PASSPHRASE, encoded).apply()
                key
            }
        }
    }
}

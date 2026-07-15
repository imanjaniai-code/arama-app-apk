package com.example.data.database

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

object DatabaseKeyManager {
    private const val PREFS_NAME = "secure_db_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase"

    private fun isRunningOnEmulator(): Boolean {
        val fingerprint = android.os.Build.FINGERPRINT
        val model = android.os.Build.MODEL
        val manufacturer = android.os.Build.MANUFACTURER
        val hardware = android.os.Build.HARDWARE
        return fingerprint.startsWith("generic") ||
                fingerprint.startsWith("unknown") ||
                model.contains("google_sdk") ||
                model.contains("Emulator") ||
                model.contains("Android SDK built for x86") ||
                manufacturer.contains("Genymotion") ||
                hardware.contains("goldfish") ||
                hardware.contains("ranchu") ||
                hardware.contains("vbox86")
    }

    fun getPassphrase(context: Context): ByteArray {
        if (isRunningOnEmulator()) {
            android.util.Log.i("DatabaseKeyManager", "Running on emulator: Bypassing secure KeyStore for maximum speed and stability.")
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedPassphrase = prefs.getString(KEY_PASSPHRASE, null)
            return if (savedPassphrase != null) {
                Base64.decode(savedPassphrase, Base64.DEFAULT)
            } else {
                val key = ByteArray(32)
                SecureRandom().nextBytes(key)
                val encoded = Base64.encodeToString(key, Base64.NO_WRAP)
                prefs.edit().putString(KEY_PASSPHRASE, encoded).apply()
                key
            }
        }

        return try {
            var result: ByteArray? = null
            var error: Throwable? = null
            val t = Thread {
                try {
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
                    result = if (savedPassphrase != null) {
                        Base64.decode(savedPassphrase, Base64.DEFAULT)
                    } else {
                        val key = ByteArray(32)
                        SecureRandom().nextBytes(key)
                        val encoded = Base64.encodeToString(key, Base64.NO_WRAP)
                        sharedPreferences.edit().putString(KEY_PASSPHRASE, encoded).apply()
                        key
                    }
                } catch (ex: Throwable) {
                    error = ex
                }
            }
            t.start()
            t.join(600) // wait at most 600 milliseconds for Keystore/EncryptedSharedPreferences
            if (t.isAlive) {
                android.util.Log.e("DatabaseKeyManager", "Database key generation timed out! Keystore might be blocked. Falling back to plain SharedPreferences.")
                t.interrupt()
                throw RuntimeException("Keystore timeout")
            }
            val err = error
            if (err != null) {
                throw err
            }
            result ?: throw RuntimeException("Null result")
        } catch (e: Throwable) {
            // Fallback to standard SharedPreferences if Keystore/EncryptedSharedPreferences fails (e.g. on custom ROMs or virtual testing environments)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedPassphrase = prefs.getString(KEY_PASSPHRASE, null)
            if (savedPassphrase != null) {
                Base64.decode(savedPassphrase, Base64.DEFAULT)
            } else {
                val key = ByteArray(32)
                SecureRandom().nextBytes(key)
                val encoded = Base64.encodeToString(key, Base64.NO_WRAP)
                prefs.edit().putString(KEY_PASSPHRASE, encoded).apply()
                key
            }
        }
    }
}

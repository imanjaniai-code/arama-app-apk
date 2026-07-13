package com.example.ui.screens

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object BiometricHelper {
    private const val TAG = "BiometricHelper"
    private const val KEY_NAME = "arama_biometric_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    fun isBiometricAvailable(context: android.content.Context): Boolean {
        return try {
            val biometricManager = BiometricManager.from(context)
            val result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            result == BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            Log.e(TAG, "Biometric availability check failed", e)
            false
        }
    }

    fun generateSecretKey() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            // Check if key already exists to avoid regenerating it unnecessarily
            if (keyStore.containsAlias(KEY_NAME)) {
                return
            }

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
            Log.d(TAG, "Generated secure biometric secret key successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate biometric secret key", e)
        }
    }

    private fun getCipher(): Cipher? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (!keyStore.containsAlias(KEY_NAME)) {
                generateSecretKey()
            }
            
            val secretKey = keyStore.getKey(KEY_NAME, null) as SecretKey
            val cipher = Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            )
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Cipher", e)
            null
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val authenticatedCipher = result.cryptoObject?.cipher
                if (authenticatedCipher != null) {
                    Log.d(TAG, "Biometric authentication succeeded with verified CryptoObject!")
                    onSuccess()
                } else {
                    Log.e(TAG, "Succeeded, but CryptoObject/Cipher is null. Cryptographic bypass suspected.")
                    onError("خطای امنیتی: تایید هویت رمزنگاری ناموفق بود.")
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e(TAG, "Biometric authentication error: $errorCode - $errString")
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.w(TAG, "Biometric authentication failed.")
                onError("تایید هویت بیومتریک ناموفق بود.")
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("تایید هویت زیست‌سنجی (آراما)")
            .setSubtitle("با اثر انگشت یا چهره خود وارد شوید")
            .setNegativeButtonText("لغو")
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        val cipher = getCipher()

        if (cipher != null) {
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } else {
            Log.e(TAG, "Cipher initialization failed, prompting without CryptoObject as fallback")
            biometricPrompt.authenticate(promptInfo)
        }
    }
}

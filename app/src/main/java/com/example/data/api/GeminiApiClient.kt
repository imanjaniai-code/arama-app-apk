package com.example.data.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"

    // Safe dynamic getter for BACKEND_URL to prevent compilation errors if not defined in .env yet
    private val BASE_URL: String
        get() {
            return try {
                val field = com.arama.app.BuildConfig::class.java.getField("BACKEND_URL")
                val value = field.get(null) as? String
                if (!value.isNullOrEmpty() && value != "BACKEND_URL_PLACEHOLDER") {
                    value.trim().removeSuffix("/")
                } else {
                    "http://10.0.2.2:3000"
                }
            } catch (e: Exception) {
                // Default loopback IP for Android Emulator to communicate with local development server
                "http://10.0.2.2:3000"
            }
        }

    // Keep custom Content and Part data classes to maintain compatibility across the codebase
    data class Content(
        val parts: List<Part>,
        val role: String? = null
    )

    data class Part(
        val text: String
    )

    // Gemini API Response Payload structures (used for streaming chunks)
    data class GeminiResponse(
        val candidates: List<Candidate>?
    )

    data class Candidate(
        val content: Content?
    )

    // Request payload structure expected by our secure node proxy
    data class ProxyRequest(
        val message: String,
        val context: String?,
        val history: List<ProxyHistoryItem>?,
        val modelMode: String
    )

    data class ProxyHistoryItem(
        val role: String,
        val text: String
    )

    // Response structure returned by our secure node proxy
    data class ProxyResponse(
        val response: String?
    )

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        prompt: String,
        history: List<Content> = emptyList(),
        systemInstructionText: String,
        modelMode: String = "general"
    ): String = withContext(Dispatchers.IO) {
        val proxyHistory = history.map { content ->
            ProxyHistoryItem(
                role = content.role ?: "user",
                text = content.parts.firstOrNull()?.text ?: ""
            )
        }

        val proxyRequest = ProxyRequest(
            message = prompt,
            context = if (systemInstructionText.isNotEmpty()) systemInstructionText else null,
            history = proxyHistory,
            modelMode = modelMode
        )

        val requestAdapter = moshi.adapter(ProxyRequest::class.java)
        val jsonRequest = requestAdapter.toJson(proxyRequest)

        val requestBody = jsonRequest.toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "$BASE_URL/chat"

        val requestBuilder = Request.Builder()
            .url(url)
            .post(requestBody)

        // Securely retrieve the current Firebase Auth ID token and attach it to the header
        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            try {
                val task = firebaseUser.getIdToken(false)
                val result = com.google.android.gms.tasks.Tasks.await(task)
                val token = result.token
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                    Log.d(TAG, "Successfully attached Firebase ID token to backend proxy request.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to retrieve Firebase ID token for Authorization header", e)
            }
        }

        val request = requestBuilder.build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e(TAG, "Proxy API call failed with code: ${response.code}, body: $bodyStr")
                    return@withContext "خطا در برقراری ارتباط با سرور میانی آراما (${response.code}). لطفاً اتصال اینترنت خود را بررسی کنید."
                }

                if (bodyStr.isNullOrEmpty()) {
                    Log.e(TAG, "Empty response body from Proxy API")
                    return@withContext "خطا در دریافت پاسخ از سرور میانی آراما."
                }

                val responseAdapter = moshi.adapter(ProxyResponse::class.java)
                val proxyResponse = responseAdapter.fromJson(bodyStr)
                val responseText = proxyResponse?.response

                if (responseText.isNullOrEmpty()) {
                    Log.e(TAG, "Failed to extract text from response structure: $bodyStr")
                    return@withContext "خطا: پاسخ دریافت شده از سرور میانی نامعتبر یا خالی بود."
                }

                return@withContext responseText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network call to Proxy API failed", e)
            "خطا در برقراری ارتباط با سرور میانی آراما. لطفاً اتصال اینترنت خود را بررسی کنید."
        }
    }

    fun generateResponseStream(
        prompt: String,
        history: List<Content> = emptyList(),
        systemInstructionText: String,
        modelMode: String = "general"
    ): Flow<String> = flow {
        val proxyHistory = history.map { content ->
            ProxyHistoryItem(
                role = content.role ?: "user",
                text = content.parts.firstOrNull()?.text ?: ""
            )
        }

        val proxyRequest = ProxyRequest(
            message = prompt,
            context = if (systemInstructionText.isNotEmpty()) systemInstructionText else null,
            history = proxyHistory,
            modelMode = modelMode
        )

        val requestAdapter = moshi.adapter(ProxyRequest::class.java)
        val jsonRequest = requestAdapter.toJson(proxyRequest)

        val requestBody = jsonRequest.toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "$BASE_URL/chat-stream"

        val requestBuilder = Request.Builder()
            .url(url)
            .post(requestBody)

        // Securely retrieve the current Firebase Auth ID token and attach it to the header
        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            try {
                val task = firebaseUser.getIdToken(false)
                val result = com.google.android.gms.tasks.Tasks.await(task)
                val token = result.token
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                    Log.d(TAG, "Successfully attached Firebase ID token to backend proxy stream request.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to retrieve Firebase ID token for Authorization header", e)
            }
        }

        val request = requestBuilder.build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "Proxy Stream API call failed with code: ${response.code}, body: $errBody")
                emit("Error: ${response.code}")
                return@flow
            }

            val source = response.body?.source()
            if (source == null) {
                emit("Error: Empty response stream")
                return@flow
            }

            val reader = BufferedReader(InputStreamReader(source.inputStream(), "UTF-8"))
            val responseAdapter = moshi.adapter(GeminiResponse::class.java)

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val trimmed = line?.trim() ?: continue
                if (trimmed.startsWith("data:")) {
                    val dataJson = trimmed.substring(5).trim()
                    if (dataJson.isEmpty() || dataJson == "[DONE]") continue
                    try {
                        val chunkResponse = responseAdapter.fromJson(dataJson)
                        val textPart = chunkResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        if (textPart != null) {
                            emit(textPart)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse stream chunk: $dataJson", e)
                    }
                }
            }
            response.close()
        } catch (e: Exception) {
            Log.e(TAG, "Network call to Proxy API failed during streaming", e)
            emit("Error: ${e.message ?: "اتصال اینترنت قطع است"}")
        }
    }.flowOn(Dispatchers.IO)
}

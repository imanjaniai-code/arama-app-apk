package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Request data classes formatted for Moshi serialization
    data class GenerateRequest(
        val contents: List<Content>,
        val systemInstruction: Content? = null,
        val generationConfig: GenerationConfig? = null
    )

    data class Content(
        val parts: List<Part>,
        val role: String? = null
    )

    data class Part(
        val text: String
    )

    data class GenerationConfig(
        val temperature: Float? = null,
        val thinkingConfig: ThinkingConfig? = null
    )

    data class ThinkingConfig(
        val thinkingLevel: String
    )

    suspend fun generateResponse(
        prompt: String,
        history: List<Content> = emptyList(),
        systemInstructionText: String,
        modelMode: String = "general"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or is default placeholder.")
            return@withContext "کلید API معتبر برای هوش مصنوعی جمینای یافت نشد. لطفا کلید خود را در منوی تنظیمات بالا یا بخش Secrets در پنل AI Studio وارد کنید."
        }

        val contentsList = mutableListOf<Content>()
        contentsList.addAll(history)
        contentsList.add(Content(listOf(Part(prompt))))

        val modelName = when (modelMode) {
            "fast" -> "gemini-3.1-flash-lite-preview"
            "complex" -> "gemini-3.1-pro-preview"
            else -> "gemini-3.5-flash"
        }

        val config = if (modelMode == "complex") {
            GenerationConfig(
                temperature = 0.7f,
                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH")
            )
        } else {
            GenerationConfig(temperature = 0.7f)
        }

        val requestData = GenerateRequest(
            contents = contentsList,
            systemInstruction = Content(listOf(Part(systemInstructionText))),
            generationConfig = config
        )

        val adapter = moshi.adapter(GenerateRequest::class.java)
        val jsonRequest = adapter.toJson(requestData)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonRequest.toRequestBody(mediaType)

        val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

        val request = Request.Builder()
            .url("$requestUrl?key=$apiKey")
            .post(body)
            .build()

        return@withContext try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorMsg = response.body?.string() ?: ""
                Log.e(TAG, "Request failed: Code ${response.code}, Message: $errorMsg")
                "متأسفانه ارتباط با سرور برقرار نشد. لطفاً چند لحظه دیگر دوباره تلاش کنید. کد خطا: ${response.code}"
            } else {
                val jsonResponse = response.body?.string()
                if (jsonResponse != null) {
                    parseResponseText(jsonResponse)
                } else {
                    "پاسخ خالی از سرور دریافت شد."
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network call failed", e)
            "خطا در برقراری ارتباط با دستیار آراما. لطفاً اتصال اینترنت خود را بررسی کنید."
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            "یک خطای غیرمنتظره رخ داد. لطفاً دوباره امتحان کنید."
        }
    }

    private fun parseResponseText(jsonString: String): String {
        return try {
            val mapAdapter = moshi.adapter(Map::class.java)
            val root = mapAdapter.fromJson(jsonString) as? Map<*, *>
            val candidates = root?.get("candidates") as? List<*>
            val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
            val content = firstCandidate?.get("content") as? Map<*, *>
            val parts = content?.get("parts") as? List<*>
            val firstPart = parts?.firstOrNull() as? Map<*, *>
            val text = firstPart?.get("text") as? String
            text ?: "متأسفانه پاسخی دریافت نشد."
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON response", e)
            "خطا در تحلیل پاسخ دریافتی."
        }
    }
}

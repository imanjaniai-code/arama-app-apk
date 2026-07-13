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

    // Keep custom Content and Part data classes to maintain compatibility across the codebase
    data class Content(
        val parts: List<Part>,
        val role: String? = null
    )

    data class Part(
        val text: String
    )

    // Gemini API Request Payload structures
    data class GeminiRequest(
        val contents: List<Content>,
        val systemInstruction: Content? = null,
        val generationConfig: GenerationConfig? = null
    )

    data class GenerationConfig(
        val temperature: Float? = null
    )

    // Gemini API Response Payload structures
    data class GeminiResponse(
        val candidates: List<Candidate>?
    )

    data class Candidate(
        val content: Content?
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
        val apiKey = com.arama.app.BuildConfig.GEMINI_API_KEY

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is missing or using placeholder")
            return@withContext "خطا: کلید API برای پیام‌رسان آراما در تنظیمات برنامه ثبت نشده است. لطفاً کلید معتبر را در بخش Secrets اضافه کنید."
        }

        // Map the user request mode to the best available official model name
        val modelName = when (modelMode) {
            "fast" -> "gemini-3.1-flash-lite-preview"
            "complex" -> "gemini-3.1-pro-preview"
            else -> "gemini-3.5-flash"
        }

        // Reconstruct conversation contents including the new user turn
        val contents = history.toMutableList()
        contents.add(
            Content(
                role = "user",
                parts = listOf(Part(text = prompt))
            )
        )

        val systemInstruction = if (systemInstructionText.isNotEmpty()) {
            Content(parts = listOf(Part(text = systemInstructionText)))
        } else {
            null
        }

        val geminiRequest = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        val requestAdapter = moshi.adapter(GeminiRequest::class.java)
        val jsonRequest = requestAdapter.toJson(geminiRequest)

        val requestBody = jsonRequest.toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini API call failed with code: ${response.code}, body: $bodyStr")
                    return@withContext "خطا در برقراری ارتباط با سرور هوش مصنوعی آراما (${response.code}). لطفاً اتصال اینترنت خود را بررسی کنید."
                }

                if (bodyStr.isNullOrEmpty()) {
                    Log.e(TAG, "Empty response body from Gemini API")
                    return@withContext "خطا در دریافت پاسخ از سرور هوش مصنوعی آراما."
                }

                val responseAdapter = moshi.adapter(GeminiResponse::class.java)
                val geminiResponse = responseAdapter.fromJson(bodyStr)
                val responseText = geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (responseText.isNullOrEmpty()) {
                    Log.e(TAG, "Failed to extract text from response structure: $bodyStr")
                    return@withContext "خطا: پاسخ دریافت شده از آراما نامعتبر یا خالی بود."
                }

                return@withContext responseText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network call to Gemini API failed", e)
            "خطا در برقراری ارتباط با سرور هوش مصنوعی آراما. لطفاً اتصال اینترنت خود را بررسی کنید."
        }
    }

    fun generateResponseStream(
        prompt: String,
        history: List<Content> = emptyList(),
        systemInstructionText: String,
        modelMode: String = "general"
    ): Flow<String> = flow {
        val apiKey = com.arama.app.BuildConfig.GEMINI_API_KEY

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is missing or using placeholder")
            emit("خطا: کلید API برای پیام‌رسان آراما در تنظیمات برنامه ثبت نشده است. لطفاً کلید معتبر را در بخش Secrets اضافه کنید.")
            return@flow
        }

        val modelName = when (modelMode) {
            "fast" -> "gemini-3.1-flash-lite-preview"
            "complex" -> "gemini-3.1-pro-preview"
            else -> "gemini-3.5-flash"
        }

        val contents = history.toMutableList()
        contents.add(
            Content(
                role = "user",
                parts = listOf(Part(text = prompt))
            )
        )

        val systemInstruction = if (systemInstructionText.isNotEmpty()) {
            Content(parts = listOf(Part(text = systemInstructionText)))
        } else {
            null
        }

        val geminiRequest = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        val requestAdapter = moshi.adapter(GeminiRequest::class.java)
        val jsonRequest = requestAdapter.toJson(geminiRequest)

        val requestBody = jsonRequest.toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:streamGenerateContent?key=$apiKey&alt=sse"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "Gemini Stream API call failed with code: ${response.code}, body: $errBody")
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
            Log.e(TAG, "Network call to Gemini API failed during streaming", e)
            emit("Error: ${e.message ?: "اتصال اینترنت قطع است"}")
        }
    }.flowOn(Dispatchers.IO)
}

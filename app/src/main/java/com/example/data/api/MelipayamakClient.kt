package com.example.data.api

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * A highly robust and direct implementation of the Melipayamak REST API client in Kotlin,
 * containing all essential methods matching the official Melipayamak SDKs on GitHub.
 * Enhanced with detailed logging and error reporting to prevent crashes.
 */
object MelipayamakClient {
    private const val TAG = "MelipayamakClient"
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Send standard SMS message.
     * Endpoint: /api/SendSMS/SendSMS
     */
    suspend fun send(
        username: String,
        password: String,
        to: String,
        from: String,
        text: String,
        isFlash: Boolean = false
    ): Result<String> {
        Log.i(TAG, "Initiating send SMS request to $to (sender line: $from)")
        return runCatching {
            val json = JSONObject().apply {
                put("username", username)
                put("password", password)
                put("to", to)
                put("from", from)
                put("text", text)
                put("isFlash", isFlash)
            }.toString()

            val request = Request.Builder()
                .url("https://rest.payamak-panel.com/api/SendSMS/SendSMS")
                .post(json.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.i(TAG, "Send SMS HTTP response status: ${response.code} (successful: ${response.isSuccessful})")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "Send SMS response body: $body")
                    body
                } else {
                    val errMessage = "HTTP Error ${response.code}: $body"
                    Log.e(TAG, "Melipayamak SMS send failed with status ${response.code}. Details: $body")
                    throw Exception(errMessage)
                }
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Exception captured in MelipayamakClient.send: ${throwable.message}", throwable)
        }
    }

    /**
     * Send SMS by Base Number (Pattern / Verification Template Code).
     * Endpoint: /api/SendSMS/BaseServiceNumber
     */
    suspend fun sendByBaseNumber(
        username: String,
        password: String,
        text: String,
        to: String,
        bodyId: Int
    ): Result<String> {
        Log.i(TAG, "Initiating sendByBaseNumber SMS request to $to (bodyId: $bodyId)")
        return runCatching {
            val json = JSONObject().apply {
                put("username", username)
                put("password", password)
                put("text", text)
                put("to", to)
                put("bodyId", bodyId)
            }.toString()

            val request = Request.Builder()
                .url("https://rest.payamak-panel.com/api/SendSMS/BaseServiceNumber")
                .post(json.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.i(TAG, "BaseServiceNumber SMS HTTP response status: ${response.code} (successful: ${response.isSuccessful})")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "BaseServiceNumber SMS response body: $body")
                    body
                } else {
                    val errMessage = "HTTP Error ${response.code}: $body"
                    Log.e(TAG, "Melipayamak BaseServiceNumber failed with status ${response.code}. Details: $body")
                    throw Exception(errMessage)
                }
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Exception captured in MelipayamakClient.sendByBaseNumber: ${throwable.message}", throwable)
        }
    }

    /**
     * Retrieve account balance / credit.
     * Endpoint: /api/SendSMS/GetCredit
     */
    suspend fun getCredit(
        username: String,
        password: String
    ): Result<Double> {
        Log.i(TAG, "Requesting Melipayamak credit balance for user: ${username.take(3)}...")
        return runCatching {
            val json = JSONObject().apply {
                put("username", username)
                put("password", password)
            }.toString()

            val request = Request.Builder()
                .url("https://rest.payamak-panel.com/api/SendSMS/GetCredit")
                .post(json.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.i(TAG, "GetCredit HTTP response status: ${response.code} (successful: ${response.isSuccessful})")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "GetCredit response body: $body")
                    val jsonResponse = JSONObject(body)
                    if (jsonResponse.has("Value")) {
                        jsonResponse.getDouble("Value")
                    } else if (jsonResponse.has("RetVal")) {
                        jsonResponse.getDouble("RetVal")
                    } else {
                        body.toDoubleOrNull() ?: throw Exception("Failed to parse credit value: $body")
                    }
                } else {
                    val errMessage = "HTTP Error ${response.code}: $body"
                    Log.e(TAG, "Melipayamak GetCredit failed with status ${response.code}. Details: $body")
                    throw Exception(errMessage)
                }
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Exception captured in MelipayamakClient.getCredit: ${throwable.message}", throwable)
        }
    }

    /**
     * Retrieve message delivery status.
     * Endpoint: /api/SendSMS/GetDelivery
     */
    suspend fun getDelivery(
        username: String,
        password: String,
        recId: Long
    ): Result<String> {
        Log.i(TAG, "Requesting Melipayamak SMS delivery status for record ID: $recId")
        return runCatching {
            val json = JSONObject().apply {
                put("username", username)
                put("password", password)
                put("recId", recId)
            }.toString()

            val request = Request.Builder()
                .url("https://rest.payamak-panel.com/api/SendSMS/GetDelivery")
                .post(json.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.i(TAG, "GetDelivery HTTP response status: ${response.code} (successful: ${response.isSuccessful})")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "GetDelivery response body: $body")
                    body
                } else {
                    val errMessage = "HTTP Error ${response.code}: $body"
                    Log.e(TAG, "Melipayamak GetDelivery failed with status ${response.code}. Details: $body")
                    throw Exception(errMessage)
                }
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Exception captured in MelipayamakClient.getDelivery: ${throwable.message}", throwable)
        }
    }

    /**
     * Retrieve inbox messages.
     * Endpoint: /api/SendSMS/GetMessages
     */
    suspend fun getMessages(
        username: String,
        password: String,
        location: Int,
        from: String,
        index: Int,
        count: Int
    ): Result<String> {
        Log.i(TAG, "Retrieving Melipayamak inbox messages from: $from (count: $count)")
        return runCatching {
            val json = JSONObject().apply {
                put("username", username)
                put("password", password)
                put("location", location)
                put("from", from)
                put("index", index)
                put("count", count)
            }.toString()

            val request = Request.Builder()
                .url("https://rest.payamak-panel.com/api/SendSMS/GetMessages")
                .post(json.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.i(TAG, "GetMessages HTTP response status: ${response.code} (successful: ${response.isSuccessful})")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "GetMessages response body: $body")
                    body
                } else {
                    val errMessage = "HTTP Error ${response.code}: $body"
                    Log.e(TAG, "Melipayamak GetMessages failed with status ${response.code}. Details: $body")
                    throw Exception(errMessage)
                }
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Exception captured in MelipayamakClient.getMessages: ${throwable.message}", throwable)
        }
    }

    /**
     * Diagnostic function to test connection and isolate network routing vs authentication issues.
     * Hits the standard 'Send' API endpoint with a dummy message to a dummy number.
     * Returns a structured diagnostic report.
     */
    suspend fun testConnection(
        username: String,
        password: String,
        from: String
    ): String {
        val sb = StringBuilder()
        sb.append("--- گزارش عیب‌یابی اتصال ملی‌پیامک ---\n")
        
        // Step 1: DNS Resolution Test
        val domain = "rest.payamak-panel.com"
        sb.append("۱. بررسی آدرس سرور (DNS):\n")
        try {
            val startTime = System.currentTimeMillis()
            val addresses = java.net.InetAddress.getAllByName(domain)
            val duration = System.currentTimeMillis() - startTime
            sb.append("   ✅ آدرس با موفقیت یافت شد (زمان: ${duration} میلی‌ثانیه)\n")
            for (addr in addresses) {
                sb.append("   📍 آی‌پی سرور: ${addr.hostAddress}\n")
            }
        } catch (e: Exception) {
            sb.append("   ❌ خطا در ترجمه آدرس سرور (DNS Lookup Failed)!\n")
            sb.append("   علت: ${e.message}\n")
            sb.append("   ⚠️ نتیجه: عدم امکان برقراری ارتباط در سطح شبکه (احتمالاً فیلترینگ یا عدم دسترسی به اینترنت).\n")
            return sb.toString()
        }

        // Step 2: HTTP Connection & API Test
        sb.append("\n۲. بررسی ارتباط وب و اعتبار سنجی (API Test):\n")
        try {
            val testJson = JSONObject().apply {
                put("username", username)
                put("password", password)
                put("to", "09000000000") // Dummy non-existent phone
                put("from", from.ifEmpty { "5000400196" })
                put("text", "تست اتصال تشخیصی آراما")
                put("isFlash", false)
            }.toString()

            val request = Request.Builder()
                .url("https://rest.payamak-panel.com/api/SendSMS/SendSMS")
                .post(testJson.toRequestBody(mediaType))
                .build()

            val startTime = System.currentTimeMillis()
            client.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startTime
                val responseBody = response.body?.string() ?: ""
                
                sb.append("   ✅ ارتباط با سرور برقرار شد (زمان پاسخ: ${duration} میلی‌ثانیه)\n")
                sb.append("   🌐 کد وضعیت HTTP: ${response.code}\n")
                
                if (response.isSuccessful) {
                    sb.append("   📩 پاسخ خام سرور: $responseBody\n")
                    
                    // Parse response value
                    var melipayamakError: String? = null
                    if (responseBody.contains("\"Value\":\"")) {
                        val valueVal = responseBody.substringAfter("\"Value\":\"").substringBefore("\"")
                        if (valueVal.startsWith("-")) melipayamakError = valueVal
                    } else if (responseBody.contains("\"RetVal\":")) {
                        val retValVal = responseBody.substringAfter("\"RetVal\":").substringBefore(",").substringBefore("}").trim()
                        if (retValVal.startsWith("-") || (retValVal != "0" && (retValVal.toIntOrNull() ?: 0) < 0)) {
                            melipayamakError = retValVal
                        }
                    } else {
                        val rawNum = responseBody.trim()
                        if (rawNum.startsWith("-") || (rawNum.toIntOrNull() ?: 0) < 0) {
                            melipayamakError = rawNum
                        }
                    }
                    
                    if (melipayamakError != null) {
                        sb.append("   ⚠️ خطا از سمت پنل ملی‌پیامک برگشت داده شد:\n")
                        sb.append("   کد خطا: $melipayamakError\n")
                        
                        val persianError = when (melipayamakError) {
                            "-1", "2" -> "نام کاربری یا رمز عبور اشتباه است (مشکل اعتبار سنجی)."
                            "-2" -> "اعتبار کافی در پنل شما وجود ندارد."
                            "-3" -> "حساب کاربری شما فعال نیست یا منقضی شده است."
                            "-4" -> "شماره فرستنده (Sender Line) نامعتبر یا غیرمجاز است."
                            "-5" -> "متن پیامک نامعتبر یا فاقد الگوی تایید شده است."
                            "-10" -> "پنل در حال حاضر مسدود شده یا غیر فعال است."
                            else -> "خطای عمومی پنل ملی‌پیامک."
                        }
                        sb.append("   توضیح: $persianError\n")
                        sb.append("   📌 نتیجه: شبکه کاملاً متصل است، اما تنظیمات پنل شما مشکل دارد.")
                    } else {
                        sb.append("   🎉 تبریک! تست اتصال کاملاً موفقیت‌آمیز بود!\n")
                        sb.append("   📌 نتیجه: هم شبکه و هم اعتبار سنجی پنل شما بدون نقص کار می‌کنند.")
                    }
                } else {
                    sb.append("   ❌ سرور به درخواست پاسخ ناموفق داد!\n")
                    sb.append("   پاسخ خطا: $responseBody\n")
                    sb.append("   📌 نتیجه: شبکه متصل است اما سرور درخواست را رد کرد.")
                }
            }
        } catch (e: Exception) {
            sb.append("   ❌ خطا در ارسال درخواست به سرور (HTTP Request Failed)!\n")
            sb.append("   نوع خطا: ${e.javaClass.simpleName}\n")
            sb.append("   جزئیات خطا: ${e.message}\n")
            sb.append("   ⚠️ نتیجه: خطای مسیریابی یا ارتباطی در سطح اینترنت رخ داده است (Connection Timeout / SSL Failure).")
        }
        
        return sb.toString()
    }
}

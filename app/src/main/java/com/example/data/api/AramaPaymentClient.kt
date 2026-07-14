package com.example.data.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object AramaPaymentClient {
    private const val TAG = "AramaPaymentClient"

    private val BASE_URL: String
        get() {
            return try {
                val field = com.arama.app.BuildConfig::class.java.getField("BACKEND_URL")
                val value = field.get(null) as? String
                if (!value.isNullOrEmpty() && value != "BACKEND_URL_PLACEHOLDER") {
                    val url = value.trim()
                    if (url.endsWith("/")) url else "$url/"
                } else {
                    "http://10.0.2.2:3000/"
                }
            } catch (e: Exception) {
                "http://10.0.2.2:3000/"
            }
        }

    data class PaymentRequest(
        val amount: Long,
        val description: String,
        val plan: String
    )

    data class PaymentResponse(
        val code: String,
        val paymentUrl: String,
        val isMock: Boolean,
        val message: String? = null
    )

    data class VerifyRequest(
        val refId: String,
        val amount: Long,
        val plan: String
    )

    data class VerifyResponse(
        val success: Boolean,
        val plan: String,
        val refId: String,
        val message: String? = null
    )

    interface AramaPaymentApi {
        @POST("payment/request")
        suspend fun requestPayment(
            @Header("Authorization") token: String,
            @Body request: PaymentRequest
        ): Response<PaymentResponse>

        @POST("payment/verify")
        suspend fun verifyPayment(
            @Header("Authorization") token: String,
            @Body request: VerifyRequest
        ): Response<VerifyResponse>
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api: AramaPaymentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AramaPaymentApi::class.java)
    }

    // Helper to fetch the Firebase ID Token securely
    private suspend fun getAuthHeader(): String {
        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            try {
                val task = firebaseUser.getIdToken(false)
                val result = com.google.android.gms.tasks.Tasks.await(task)
                val token = result.token
                if (!token.isNullOrEmpty()) {
                    return "Bearer $token"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to retrieve Firebase ID token", e)
            }
        }
        return ""
    }

    suspend fun requestPayment(
        amount: Long,
        description: String,
        plan: String
    ): Result<PaymentResponse> {
        return try {
            val token = getAuthHeader()
            val response = api.requestPayment(token, PaymentRequest(amount, description, plan))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "خطای ناشناخته در درگاه پرداخت"
                Log.e(TAG, "Request payment failed: ${response.code()} - $errorBody")
                Result.failure(Exception("خطا در درخواست پرداخت: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request payment exception", e)
            Result.failure(e)
        }
    }

    suspend fun verifyPayment(
        refId: String,
        amount: Long,
        plan: String
    ): Result<VerifyResponse> {
        return try {
            val token = getAuthHeader()
            val response = api.verifyPayment(token, VerifyRequest(refId, amount, plan))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "خطای ناشناخته در تأیید پرداخت"
                Log.e(TAG, "Verify payment failed: ${response.code()} - $errorBody")
                Result.failure(Exception("خطا در تأیید پرداخت: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Verify payment exception", e)
            Result.failure(e)
        }
    }
}

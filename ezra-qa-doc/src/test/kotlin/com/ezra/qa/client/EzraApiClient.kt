package com.ezra.qa.client

import com.ezra.qa.config.TestConfig
import com.ezra.qa.model.request.*
import com.ezra.qa.model.response.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class EzraApiClient {

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaType()
    }

    val mapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val http = OkHttpClient.Builder()
        .connectTimeout(TestConfig.httpTimeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(TestConfig.httpTimeoutSeconds, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor { println("[HTTP] $it") }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    fun authenticate(email: String, password: String): Pair<Response, String> {
        val body = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .add("grant_type", "password")
            .add("client_id", "52A25F89-0EDB-4E23-AD45-5B026D103E2E")
            .add("scope", "openid email profile roles offline_access")
            .build()
        return execute(Request.Builder()
            .url("${TestConfig.baseUrl}/platform/api/auth/connect/token")
            .post(body).build())
    }

    fun parseAuth(body: String): AuthResponse = mapper.readValue(body)

    fun postBookingStage(token: String, req: BookingStageRequest): Pair<Response, String> =
        execute(Request.Builder()
            .url("${TestConfig.baseUrl}/platform/api/members/bookingstage")
            .bearer(token).post(req.toJson()).build())

    fun parseBookingStage(body: String): BookingStageResponse = mapper.readValue(body)

    fun createEncounterHub(token: String, req: CreateEncounterHubRequest): Pair<Response, String> =
        execute(Request.Builder()
            .url("${TestConfig.baseUrl}/packages/api/encounter/hub")
            .bearer(token).post(req.toJson()).build())

    fun parseEncounter(body: String): EncounterResponse = mapper.readValue(body)

    fun getEncounter(token: String, encounterId: String): Pair<Response, String> =
        execute(Request.Builder()
            .url("${TestConfig.baseUrl}/packages/api/encounter/$encounterId")
            .bearer(token).get().build())

    fun createPendingPayment(token: String, encounterId: String, req: CreatePendingPaymentRequest): Pair<Response, String> =
        execute(Request.Builder()
            .url("${TestConfig.baseUrl}/packages/api/payments/$encounterId/create-pending")
            .bearer(token).post(req.toJson()).build())

    fun parsePendingPayment(body: String): PendingPaymentResponse = mapper.readValue(body)

    private fun execute(request: Request): Pair<Response, String> {
        val response = http.newCall(request).execute()
        val body = response.body?.string() ?: ""
        return response to body
    }

    private fun Any.toJson() = mapper.writeValueAsString(this).toRequestBody(JSON)
    private fun Request.Builder.bearer(token: String) = header("Authorization", "Bearer $token")
}

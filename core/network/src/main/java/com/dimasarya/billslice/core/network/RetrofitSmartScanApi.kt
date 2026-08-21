package com.dimasarya.billslice.core.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private interface RetrofitSmartScanService {
    @POST
    suspend fun parse(
        @Url endpointUrl: String,
        @Header("apikey") publishableKey: String,
        @Body request: SmartScanParseRequestDto,
    ): Response<SmartScanParseResponseDto>
}

class RetrofitSmartScanApi private constructor(
    private val service: RetrofitSmartScanService,
    private val endpointUrl: String,
    private val publishableKey: String,
    private val json: Json,
) : SmartScanApi {
    override suspend fun parse(request: SmartScanParseRequestDto): SmartScanParseResponseDto {
        val response = try {
            service.parse(endpointUrl, publishableKey, request)
        } catch (exception: SerializationException) {
            throw SmartScanProtocolException(exception)
        }
        val envelope = response.body() ?: response.errorBody()?.string()?.let { body ->
            runCatching { json.decodeFromString<SmartScanParseResponseDto>(body) }.getOrNull()
        } ?: throw SmartScanProtocolException()
        if (!response.matchesContract(envelope)) throw SmartScanProtocolException()
        return envelope
    }

    companion object {
        fun create(
            endpoint: SmartScanEndpoint,
            json: Json = SmartScanJson.codec,
            client: OkHttpClient = defaultClient(),
        ): RetrofitSmartScanApi = create(endpoint.url, endpoint.publishableKey, json, client)

        internal fun createForTesting(
            endpointUrl: String,
            publishableKey: String,
            json: Json = SmartScanJson.codec,
            client: OkHttpClient = defaultClient(),
        ): RetrofitSmartScanApi = create(endpointUrl, publishableKey, json, client)

        private fun create(
            endpointUrl: String,
            publishableKey: String,
            json: Json,
            client: OkHttpClient,
        ): RetrofitSmartScanApi {
            val service = Retrofit.Builder()
                .baseUrl("https://localhost/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(RetrofitSmartScanService::class.java)
            return RetrofitSmartScanApi(service, endpointUrl, publishableKey, json)
        }

        private fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build()
    }
}

class SmartScanProtocolException(cause: Throwable? = null) : Exception(cause)

private fun Response<SmartScanParseResponseDto>.matchesContract(envelope: SmartScanParseResponseDto): Boolean {
    if (isSuccessful) return code() == 200 && envelope.status == "success" && envelope.error == null
    if (envelope.status != "error" || envelope.draft != null || envelope.warnings.isNotEmpty() || envelope.error == null) return false
    if ((envelope.error.code == "QUOTA_EXHAUSTED") != (envelope.quota != null)) return false
    val contract = errorContracts[envelope.error.code] ?: return false
    if (contract.httpCode != code() || contract.retryable != envelope.error.retryable) return false
    val retryAfter = envelope.error.retryAfterSeconds
    if (retryAfter != null && retryAfter <= 0) return false
    if (!envelope.error.retryable && retryAfter != null) return false
    if (contract.delayRequired && retryAfter == null) return false
    return true
}

private data class ErrorContract(val httpCode: Int, val retryable: Boolean, val delayRequired: Boolean = false)

private val errorContracts = mapOf(
    "INVALID_REQUEST" to ErrorContract(400, false),
    "INVALID_API_KEY" to ErrorContract(401, false),
    "REQUEST_IN_PROGRESS" to ErrorContract(409, true, delayRequired = true),
    "REQUEST_CONFLICT" to ErrorContract(409, false),
    "REPLAY_EXPIRED" to ErrorContract(410, false),
    "OCR_UNUSABLE" to ErrorContract(422, false),
    "PARSE_UNUSABLE" to ErrorContract(422, true),
    "QUOTA_EXHAUSTED" to ErrorContract(429, false),
    "RATE_LIMITED" to ErrorContract(429, true, delayRequired = true),
    "INTERNAL" to ErrorContract(500, true),
    "PARSER_UPSTREAM_FAILURE" to ErrorContract(502, true),
    "PARSER_RESPONSE_INVALID" to ErrorContract(502, true),
    "POLICY_UNAVAILABLE" to ErrorContract(503, true),
    "SERVICE_UNAVAILABLE" to ErrorContract(503, true),
    "PARSER_TIMEOUT" to ErrorContract(504, true),
)

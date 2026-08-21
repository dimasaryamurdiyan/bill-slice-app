package com.dimasarya.billslice.core.network

import com.dimasarya.billslice.core.model.ReceiptParseRequest
import java.net.URI
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class SmartScanEndpoint(
    val url: String,
    val publishableKey: String,
) {
    init {
        val endpoint = runCatching { URI(url) }.getOrNull()
        require(
            endpoint?.scheme == "https" &&
                !endpoint.host.isNullOrBlank() &&
                endpoint.host.endsWith(".supabase.co") &&
                endpoint.userInfo == null &&
                endpoint.port == -1 &&
                endpoint.path == "/functions/v1/smart-scan-parse" &&
                endpoint.rawQuery == null &&
                endpoint.fragment == null,
        ) { "Smart Scan endpoint must be the HTTPS parse function URL" }
        require(publishableKey.isNotBlank()) { "Publishable key cannot be blank" }
    }
}

@Serializable
data class SmartScanParseRequestDto(
    val requestId: String,
    val installId: String,
    val locale: String,
    val currency: String,
    val timezone: String,
    val ocrText: String,
)

fun ReceiptParseRequest.toDto(): SmartScanParseRequestDto = SmartScanParseRequestDto(
    requestId = requestId,
    installId = installId,
    locale = locale,
    currency = currency.code,
    timezone = timezone,
    ocrText = ocrText,
)

interface SmartScanApi {
    suspend fun parse(request: SmartScanParseRequestDto): SmartScanParseResponseDto
}

object SmartScanJson {
    val codec: Json = Json {
        ignoreUnknownKeys = false
        explicitNulls = true
    }
}

@Serializable
data class SmartScanErrorDto(
    val code: String,
    val retryable: Boolean,
    val retryAfterSeconds: Int?,
)

@Serializable
data class SmartScanWarningDto(
    val code: String,
    val field: String?,
)

@Serializable
data class SmartScanQuotaDto(
    val plan: String,
    val policy: String,
    val used: Int,
    val limit: Int?,
    val remaining: Int?,
    val resetsAt: String?,
)

@Serializable
data class SmartScanItemDto(
    val name: String?,
    val quantity: Int?,
    val unitPriceMinor: Long?,
    val lineTotalMinor: Long?,
)

@Serializable
data class SmartScanDraftDto(
    val merchantName: String?,
    val purchasedAt: String?,
    val currency: String,
    val items: List<SmartScanItemDto>,
    val serviceRateBasisPoints: Int?,
    val serviceAmountMinor: Long?,
    val taxRateBasisPoints: Int?,
    val taxAmountMinor: Long?,
    val discountMinor: Long?,
    val receiptTotalMinor: Long?,
)

@Serializable
data class SmartScanParseResponseDto(
    val requestId: String?,
    val status: String,
    val draft: SmartScanDraftDto? = null,
    val warnings: List<SmartScanWarningDto> = emptyList(),
    val quota: SmartScanQuotaDto?,
    val error: SmartScanErrorDto? = null,
)

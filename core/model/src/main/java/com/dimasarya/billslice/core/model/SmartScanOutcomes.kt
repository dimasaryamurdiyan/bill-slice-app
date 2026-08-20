package com.dimasarya.billslice.core.model

import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class ReceiptParseRequest(
    val requestId: String,
    val installId: String,
    val locale: String,
    val currency: CurrencyCode,
    val timezone: String,
    val ocrText: String,
) {
    init {
        require(requestId.isUuid()) { "Request ID must be a UUID" }
        require(installId.isUuid()) { "Install ID must be a UUID" }
        require(locale.isBcp47LanguageTag()) { "Locale must be a BCP 47 language tag" }
        require(timezone.isIanaTimezone()) { "Timezone must be an IANA timezone" }
        require(ocrText.isNotBlank()) { "OCR text cannot be blank" }
    }
}

private fun String.isUuid(): Boolean = runCatching { UUID.fromString(this).toString().equals(this, ignoreCase = true) }
    .getOrDefault(false)

private fun String.isBcp47LanguageTag(): Boolean {
    if (isBlank()) return false
    return runCatching {
        val parsed = Locale.Builder().setLanguageTag(this).build()
        parsed.language.isNotBlank() && parsed.toLanguageTag().equals(this, ignoreCase = true)
    }.getOrDefault(false)
}

private fun String.isIanaTimezone(): Boolean =
    this in TimeZone.getAvailableIDs() && (this == "UTC" || this == "GMT" || IANA_AREA_TIMEZONE.matches(this))

private val IANA_AREA_TIMEZONE = Regex(
    """(?:Africa|America|Antarctica|Arctic|Asia|Atlantic|Australia|Europe|Indian|Pacific|Etc)/[A-Za-z0-9_+.-]+(?:/[A-Za-z0-9_+.-]+)*""",
)

sealed interface ReceiptOcrOutcome {
    data class Success(val text: String) : ReceiptOcrOutcome
    data object ModelDownloadFailed : ReceiptOcrOutcome
    data object RecognitionFailed : ReceiptOcrOutcome
    data object Cancelled : ReceiptOcrOutcome
    data object UnexpectedFailure : ReceiptOcrOutcome
}

data class ReceiptParseWarning(
    val code: String,
    val field: String?,
)

data class ParsedReceiptItem(
    val id: String,
    val name: String?,
    val quantity: Int?,
    val unitPriceMinor: Long?,
    val lineTotalMinor: Long?,
)

data class ParsedReceiptDraft(
    val id: String,
    val merchantName: String?,
    val purchasedAt: String?,
    val currency: CurrencyCode,
    val items: List<ParsedReceiptItem>,
    val serviceRateBasisPoints: Int?,
    val serviceAmountMinor: Long?,
    val taxRateBasisPoints: Int?,
    val taxAmountMinor: Long?,
    val discountMinor: Long?,
    val receiptTotalMinor: Long?,
)

sealed interface SmartScanParseOutcome {
    data class Success(
        val draft: ParsedReceiptDraft,
        val warnings: List<ReceiptParseWarning>,
        val quota: SmartScanQuota,
    ) : SmartScanParseOutcome

    data class Failure(
        val failure: SmartScanParseFailure,
    ) : SmartScanParseOutcome
}

sealed interface SmartScanParseFailure {
    data object Offline : SmartScanParseFailure
    data object Timeout : SmartScanParseFailure
    data object PolicyUnavailable : SmartScanParseFailure
    data object ReplayExpired : SmartScanParseFailure
    data class ServerRejected(
        val code: String,
        val retryable: Boolean,
        val retryAfterSeconds: Int?,
    ) : SmartScanParseFailure
    data class QuotaExceeded(val quota: SmartScanQuota) : SmartScanParseFailure
    data object MalformedResponse : SmartScanParseFailure
    data object Cancelled : SmartScanParseFailure
    data object ConfigurationUnavailable : SmartScanParseFailure
    data object UnexpectedFailure : SmartScanParseFailure
}

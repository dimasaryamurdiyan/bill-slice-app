package com.dimasarya.billslice.core.model

data class ReceiptParseRequest(
    val installId: String,
    val locale: String,
    val currency: CurrencyCode,
    val timezone: String,
    val ocrText: String,
) {
    init {
        require(installId.isNotBlank()) { "Install ID cannot be blank" }
        require(locale.isNotBlank()) { "Locale cannot be blank" }
        require(timezone.isNotBlank()) { "Timezone cannot be blank" }
        require(ocrText.isNotBlank()) { "OCR text cannot be blank" }
    }
}

sealed interface ReceiptOcrOutcome {
    data class Success(val text: String) : ReceiptOcrOutcome
    data object ModelDownloadFailed : ReceiptOcrOutcome
    data object RecognitionFailed : ReceiptOcrOutcome
    data object Cancelled : ReceiptOcrOutcome
    data object UnexpectedFailure : ReceiptOcrOutcome
}

data class ReceiptParseWarning(
    val code: String,
    val message: String,
)

sealed interface SmartScanParseOutcome {
    data class Success(
        val draft: BillDraft,
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
    data object ServerRejected : SmartScanParseFailure
    data class QuotaExceeded(val quota: SmartScanQuota) : SmartScanParseFailure
    data object MalformedResponse : SmartScanParseFailure
    data object Cancelled : SmartScanParseFailure
    data object ConfigurationUnavailable : SmartScanParseFailure
    data object UnexpectedFailure : SmartScanParseFailure
}

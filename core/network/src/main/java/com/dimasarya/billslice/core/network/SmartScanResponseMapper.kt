package com.dimasarya.billslice.core.network

import com.dimasarya.billslice.core.model.ParsedReceiptDraft
import com.dimasarya.billslice.core.model.ParsedReceiptItem
import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.ReceiptParseWarning
import com.dimasarya.billslice.core.model.SmartScanParseFailure
import com.dimasarya.billslice.core.model.SmartScanParseOutcome
import com.dimasarya.billslice.core.model.SmartScanQuota
import java.time.OffsetDateTime

fun SmartScanParseResponseDto.toOutcome(request: ReceiptParseRequest): SmartScanParseOutcome {
    val isUncorrelatedInvalidRequest = requestId == null && status == "error" && error?.code == "INVALID_REQUEST"
    if (requestId != request.requestId && !isUncorrelatedInvalidRequest) {
        return SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    }
    if (status != "success") return SmartScanParseOutcome.Failure(error.toFailure(quota))
    val parsedDraft = draft ?: return SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    if (!parsedDraft.currency.equals(request.currency.code, ignoreCase = true)) {
        return SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    }
    if (!parsedDraft.isValid(warnings)) {
        return SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    }
    val hasUsableItem = parsedDraft.items.any { item ->
        !item.name.isNullOrBlank() &&
            item.quantity != null && item.quantity > 0 &&
            listOfNotNull(item.unitPriceMinor, item.lineTotalMinor).any { it >= 0 }
    }
    if (!hasUsableItem) return SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    val responseQuota = quota?.toQuota()
        ?: return SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    return SmartScanParseOutcome.Success(
        draft = ParsedReceiptDraft(
            id = request.requestId,
            merchantName = parsedDraft.merchantName,
            purchasedAt = parsedDraft.purchasedAt,
            currency = request.currency,
            items = parsedDraft.items.mapIndexed { index, item ->
                ParsedReceiptItem(
                    id = "${request.requestId}-item-$index",
                    name = item.name,
                    quantity = item.quantity,
                    unitPriceMinor = item.unitPriceMinor,
                    lineTotalMinor = item.lineTotalMinor,
                )
            },
            serviceRateBasisPoints = parsedDraft.serviceRateBasisPoints,
            serviceAmountMinor = parsedDraft.serviceAmountMinor,
            taxRateBasisPoints = parsedDraft.taxRateBasisPoints,
            taxAmountMinor = parsedDraft.taxAmountMinor,
            discountMinor = parsedDraft.discountMinor,
            receiptTotalMinor = parsedDraft.receiptTotalMinor,
        ),
        warnings = warnings.map { ReceiptParseWarning(it.code, it.field) },
        quota = responseQuota,
    )
}

private fun SmartScanErrorDto?.toFailure(quota: SmartScanQuotaDto?): SmartScanParseFailure = when (this?.code) {
    "QUOTA_EXHAUSTED" -> quota?.toQuota()?.takeIf { it.remainingScans == 0 }?.let(SmartScanParseFailure::QuotaExceeded)
        ?: SmartScanParseFailure.MalformedResponse
    "PARSER_TIMEOUT" -> SmartScanParseFailure.Timeout
    "INVALID_API_KEY" -> SmartScanParseFailure.ConfigurationUnavailable
    "POLICY_UNAVAILABLE" -> SmartScanParseFailure.PolicyUnavailable
    "REPLAY_EXPIRED" -> SmartScanParseFailure.ReplayExpired
    "REQUEST_IN_PROGRESS", "REQUEST_CONFLICT", "INVALID_REQUEST", "OCR_UNUSABLE", "PARSE_UNUSABLE",
    "RATE_LIMITED", "PARSER_UPSTREAM_FAILURE", "PARSER_RESPONSE_INVALID", "SERVICE_UNAVAILABLE", "INTERNAL" ->
        SmartScanParseFailure.ServerRejected(code, retryable, retryAfterSeconds)
    else -> SmartScanParseFailure.ServerRejected(
        code = this?.code ?: "UNKNOWN",
        retryable = this?.retryable ?: false,
        retryAfterSeconds = this?.retryAfterSeconds,
    )
}

private fun SmartScanQuotaDto.toQuota(): SmartScanQuota? {
    if (!((plan == "free" && policy == "monthly_5") || (plan == "pro" && policy == "fair_use"))) return null
    if (plan == "free" && limit != 5) return null
    if ((limit == null) != (remaining == null)) return null
    if (limit != null && remaining != null && used.toLong() + remaining.toLong() != limit.toLong()) return null
    if ((policy == "monthly_5") != (resetsAt != null)) return null
    val resetEpochMillis = resetsAt?.toRfc3339EpochMillis() ?: if (resetsAt == null) null else return null
    return runCatching {
        SmartScanQuota(
            remainingScans = remaining,
            resetsAtEpochMillis = resetEpochMillis,
            plan = plan,
            policy = policy,
            usedScans = used,
            limit = limit,
        )
    }.getOrNull()
}

private fun SmartScanDraftDto.isValid(warnings: List<SmartScanWarningDto>): Boolean {
    if (merchantName != null && merchantName.isBlank()) return false
    if (merchantName == null && !warnings.has("MERCHANT_UNCERTAIN", "/draft/merchantName")) return false
    if (purchasedAt != null && purchasedAt.toRfc3339EpochMillis() == null) return false
    if (purchasedAt == null && !warnings.has("PURCHASED_AT_UNCERTAIN", "/draft/purchasedAt")) return false
    if (items.isEmpty()) return false
    items.forEachIndexed { index, item ->
        val path = "/draft/items/$index"
        if (item.name != null && item.name.isBlank()) return false
        if (item.name == null && !warnings.has("ITEM_NAME_UNCERTAIN", "$path/name")) return false
        if (item.quantity != null && item.quantity <= 0) return false
        if (item.quantity == null && !warnings.has("ITEM_QUANTITY_UNCERTAIN", "$path/quantity")) return false
        if (item.unitPriceMinor != null && item.unitPriceMinor < 0) return false
        if (item.unitPriceMinor == null && !warnings.has("ITEM_PRICE_UNCERTAIN", "$path/unitPriceMinor")) return false
        if (item.lineTotalMinor != null && item.lineTotalMinor < 0) return false
        if (item.lineTotalMinor == null && !warnings.has("ITEM_PRICE_UNCERTAIN", "$path/lineTotalMinor")) return false
    }
    if (!nonNegative(serviceRateBasisPoints, serviceAmountMinor)) return false
    if ((serviceRateBasisPoints == null || serviceAmountMinor == null) && !warnings.has("SERVICE_UNCERTAIN")) return false
    if (!nonNegative(taxRateBasisPoints, taxAmountMinor)) return false
    if ((taxRateBasisPoints == null || taxAmountMinor == null) && !warnings.has("TAX_UNCERTAIN")) return false
    if (discountMinor != null && discountMinor < 0) return false
    if (discountMinor == null && !warnings.has("DISCOUNT_UNCERTAIN", "/draft/discountMinor")) return false
    if (receiptTotalMinor != null && receiptTotalMinor < 0) return false
    if (receiptTotalMinor == null && !warnings.has("TOTAL_MISSING", "/draft/receiptTotalMinor")) return false
    return warnings.all { it.field == null || it.field.isJsonPointer() }
}

private fun nonNegative(vararg values: Number?): Boolean = values.all { it == null || it.toLong() >= 0 }

private fun List<SmartScanWarningDto>.has(code: String, field: String? = null): Boolean = any { warning ->
    warning.code == code && (field == null || warning.field == null || warning.field == field)
}

private fun String.toRfc3339EpochMillis(): Long? {
    if (!RFC_3339.matches(this)) return null
    return runCatching { OffsetDateTime.parse(this).toInstant().toEpochMilli() }.getOrNull()
}

private fun String.isJsonPointer(): Boolean {
    if (!startsWith('/')) return false
    var index = 0
    while (index < length) {
        if (this[index] == '~') {
            if (index + 1 >= length || this[index + 1] !in charArrayOf('0', '1')) return false
            index++
        }
        index++
    }
    return true
}

private val RFC_3339 = Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d{1,9})?(?:Z|[+-]\d{2}:\d{2})""")

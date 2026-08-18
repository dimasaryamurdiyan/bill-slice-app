package com.dimasarya.billslice.core.model

data class ReceiptTotalsValidationResult(
    val status: ReceiptTotalStatus,
    val calculatedTotal: Money,
    val receiptTotal: Money?,
    val difference: Money,
)

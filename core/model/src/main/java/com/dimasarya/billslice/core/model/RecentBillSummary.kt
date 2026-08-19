package com.dimasarya.billslice.core.model

data class RecentBillSummary(
    val id: String,
    val merchantName: String,
    val createdAtEpochMillis: Long,
    val total: Money,
    val participantCount: Int,
    val currency: CurrencyCode = CurrencyCode.IDR,
)

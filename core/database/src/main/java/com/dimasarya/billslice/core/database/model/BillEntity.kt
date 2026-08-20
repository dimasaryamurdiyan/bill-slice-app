package com.dimasarya.billslice.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey
    val id: String,
    val merchantName: String,
    val currency: String,
    val serviceRateBasisPoints: Long,
    val taxRateBasisPoints: Long,
    val discountMinorUnits: Long,
    val receiptTotalMinorUnits: Long?,
    val payerId: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val calculatedSubtotalMinorUnits: Long,
    val calculatedServiceMinorUnits: Long,
    val calculatedTaxMinorUnits: Long,
    val calculatedDiscountMinorUnits: Long,
    val calculatedRoundingMinorUnits: Long,
    val calculatedTotalMinorUnits: Long,
)

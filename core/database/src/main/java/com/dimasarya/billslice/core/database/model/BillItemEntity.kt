package com.dimasarya.billslice.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bill_items",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["billId"])],
)
data class BillItemEntity(
    @PrimaryKey
    val id: String,
    val billId: String,
    val name: String,
    val unitPriceMinorUnits: Long,
    val quantity: Int,
)

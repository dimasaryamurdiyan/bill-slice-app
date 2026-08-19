package com.dimasarya.billslice.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_assignments",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["billId"]),
        Index(value = ["itemId"]),
    ],
)
data class ItemAssignmentEntity(
    @PrimaryKey
    val id: String,
    val billId: String,
    val itemId: String,
    val participantId: String,
)

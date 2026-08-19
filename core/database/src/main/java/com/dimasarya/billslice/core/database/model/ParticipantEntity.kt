package com.dimasarya.billslice.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "participants",
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
data class ParticipantEntity(
    @PrimaryKey
    val id: String,
    val billId: String,
    val name: String,
)

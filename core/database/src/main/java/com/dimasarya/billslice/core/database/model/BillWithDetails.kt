package com.dimasarya.billslice.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class BillWithDetails(
    @Embedded val bill: BillEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "billId",
    )
    val items: List<BillItemEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "billId",
    )
    val participants: List<ParticipantEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "billId",
    )
    val assignments: List<ItemAssignmentEntity>,
)

package com.dimasarya.billslice.core.data.mapper

import com.dimasarya.billslice.core.database.model.BillEntity
import com.dimasarya.billslice.core.database.model.BillItemEntity
import com.dimasarya.billslice.core.database.model.BillWithDetails
import com.dimasarya.billslice.core.database.model.ItemAssignmentEntity
import com.dimasarya.billslice.core.database.model.ParticipantEntity
import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ItemAssignment
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.core.model.Rate
import com.dimasarya.billslice.core.model.RecentBillSummary

object BillEntityMapper {

    fun toEntities(
        draft: BillDraft,
        result: BillCalculationResult,
    ): Pair<BillEntity, Triple<List<BillItemEntity>, List<ParticipantEntity>, List<ItemAssignmentEntity>>> {
        val now = System.currentTimeMillis()
        val createdAt = if (draft.createdAtEpochMillis > 0) draft.createdAtEpochMillis else now
        val updatedAt = now

        val billEntity = BillEntity(
            id = draft.id,
            merchantName = draft.merchantName,
            currency = draft.currency.code,
            serviceRateBasisPoints = draft.serviceRate.basisPoints,
            taxRateBasisPoints = draft.taxRate.basisPoints,
            discountMinorUnits = draft.discount.amountMinor,
            receiptTotalMinorUnits = draft.receiptTotal?.amountMinor,
            payerId = draft.payerId,
            createdAtEpochMillis = createdAt,
            updatedAtEpochMillis = updatedAt,
            calculatedSubtotalMinorUnits = result.subtotal.amountMinor,
            calculatedServiceMinorUnits = result.serviceAmount.amountMinor,
            calculatedTaxMinorUnits = result.taxAmount.amountMinor,
            calculatedDiscountMinorUnits = result.discountAmount.amountMinor,
            calculatedRoundingMinorUnits = result.roundingAdjustment.amountMinor,
            calculatedTotalMinorUnits = result.total.amountMinor,
        )

        val itemEntities = draft.items.map { item ->
            BillItemEntity(
                id = item.id,
                billId = draft.id,
                name = item.name,
                unitPriceMinorUnits = item.unitPrice.amountMinor,
                quantity = item.quantity,
            )
        }

        val participantEntities = draft.participants.map { participant ->
            ParticipantEntity(
                id = participant.id,
                billId = draft.id,
                name = participant.name,
            )
        }

        val assignmentEntities = draft.assignments.map { assignment ->
            ItemAssignmentEntity(
                id = "${draft.id}_${assignment.itemId}",
                billId = draft.id,
                itemId = assignment.itemId,
                participantId = assignment.participantId,
            )
        }

        return billEntity to Triple(itemEntities, participantEntities, assignmentEntities)
    }

    fun toDomainDraft(details: BillWithDetails): BillDraft {
        val currency = CurrencyCode.fromCode(details.bill.currency) ?: CurrencyCode.IDR
        val items = details.items.map { itemEntity ->
            BillItem(
                id = itemEntity.id,
                name = itemEntity.name,
                unitPrice = Money(itemEntity.unitPriceMinorUnits, currency),
                quantity = itemEntity.quantity.coerceAtLeast(1),
            )
        }
        val participants = details.participants.map { participantEntity ->
            Participant(
                id = participantEntity.id,
                name = participantEntity.name,
            )
        }
        val assignments = details.assignments.map { assignmentEntity ->
            ItemAssignment(
                itemId = assignmentEntity.itemId,
                participantId = assignmentEntity.participantId,
            )
        }

        return BillDraft(
            id = details.bill.id,
            merchantName = details.bill.merchantName,
            currency = currency,
            items = items,
            participants = participants,
            assignments = assignments,
            payerId = details.bill.payerId ?: participants.firstOrNull()?.id,
            serviceRate = Rate(details.bill.serviceRateBasisPoints),
            taxRate = Rate(details.bill.taxRateBasisPoints),
            discount = Money(details.bill.discountMinorUnits, currency),
            receiptTotal = details.bill.receiptTotalMinorUnits?.let { Money(it, currency) },
            createdAtEpochMillis = details.bill.createdAtEpochMillis,
            updatedAtEpochMillis = details.bill.updatedAtEpochMillis,
        )
    }

    fun toSummaryOrNull(details: BillWithDetails): RecentBillSummary? {
        return try {
            val currency = CurrencyCode.fromCode(details.bill.currency) ?: CurrencyCode.IDR
            RecentBillSummary(
                id = details.bill.id,
                merchantName = details.bill.merchantName.ifBlank { "Untitled Bill" },
                createdAtEpochMillis = details.bill.createdAtEpochMillis,
                total = Money(details.bill.calculatedTotalMinorUnits, currency),
                participantCount = details.participants.size,
                currency = currency,
            )
        } catch (_: Exception) {
            null
        }
    }
}

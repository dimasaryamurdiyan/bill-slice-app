package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.core.model.ParticipantSplit
import java.math.BigDecimal
import java.math.RoundingMode

class CalculateBillSplitUseCase(
    private val validateBillDraftUseCase: ValidateBillDraftUseCase = ValidateBillDraftUseCase(),
) {

    operator fun invoke(draft: BillDraft): BillCalculationResult {
        val errors = validateBillDraftUseCase(draft)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Cannot calculate invalid bill draft: $errors")
        }

        val payer = draft.participants.first { it.id == draft.payerId }
        val currency = draft.currency

        val subtotalMinor = draft.subtotal.amountMinor
        val subtotalBD = BigDecimal(subtotalMinor)

        val serviceRateBD = BigDecimal(draft.serviceRate.basisPoints).divide(BigDecimal(10000), 8, RoundingMode.HALF_UP)
        val taxRateBD = BigDecimal(draft.taxRate.basisPoints).divide(BigDecimal(10000), 8, RoundingMode.HALF_UP)
        val discountBD = BigDecimal(draft.discount.amountMinor)

        // Subtotal, Service, Tax, Total calculations for the whole bill
        val serviceTotalBD = subtotalBD.multiply(serviceRateBD)
        val subtotalPlusServiceBD = subtotalBD.add(serviceTotalBD)
        val taxTotalBD = subtotalPlusServiceBD.multiply(taxRateBD)
        val grossTotalBD = subtotalPlusServiceBD.add(taxTotalBD)
        val netTotalBD = grossTotalBD.subtract(discountBD).max(BigDecimal.ZERO)

        val serviceTotalMinor = serviceTotalBD.setScale(0, RoundingMode.HALF_UP).toLong()
        val taxTotalMinor = taxTotalBD.setScale(0, RoundingMode.HALF_UP).toLong()
        val totalMinor = netTotalBD.setScale(0, RoundingMode.HALF_UP).toLong()

        // Group assignments by participant
        val assignmentsByParticipant = draft.assignments.groupBy { it.participantId }
        val itemsById = draft.items.associateBy { it.id }

        data class ParticipantIntermediate(
            val participant: Participant,
            val itemSubtotalMinor: Long,
            val serviceMinor: Long,
            val taxMinor: Long,
            val discountMinor: Long,
            val roundedTotalMinor: Long,
        )

        val intermediates = draft.participants.map { participant ->
            val participantAssignments = assignmentsByParticipant[participant.id] ?: emptyList()
            val participantItems = participantAssignments.mapNotNull { itemsById[it.itemId] }
            val itemSubtotalMinor = participantItems.fold(0L) { acc, item -> acc + item.subtotal.amountMinor }

            if (subtotalMinor == 0L) {
                ParticipantIntermediate(
                    participant = participant,
                    itemSubtotalMinor = 0L,
                    serviceMinor = 0L,
                    taxMinor = 0L,
                    discountMinor = 0L,
                    roundedTotalMinor = 0L,
                )
            } else {
                val itemSubtotalBD = BigDecimal(itemSubtotalMinor)
                val fractionBD = itemSubtotalBD.divide(subtotalBD, 10, RoundingMode.HALF_UP)

                val pServiceBD = itemSubtotalBD.multiply(serviceRateBD)
                val pSubtotalPlusServiceBD = itemSubtotalBD.add(pServiceBD)
                val pTaxBD = pSubtotalPlusServiceBD.multiply(taxRateBD)
                val pDiscountBD = discountBD.multiply(fractionBD)

                val pTotalBD = itemSubtotalBD
                    .add(pServiceBD)
                    .add(pTaxBD)
                    .subtract(pDiscountBD)
                    .max(BigDecimal.ZERO)

                val pServiceMinor = pServiceBD.setScale(0, RoundingMode.HALF_UP).toLong()
                val pTaxMinor = pTaxBD.setScale(0, RoundingMode.HALF_UP).toLong()
                val pDiscountMinor = pDiscountBD.setScale(0, RoundingMode.HALF_UP).toLong()
                val pRoundedTotalMinor = pTotalBD.setScale(0, RoundingMode.HALF_UP).toLong()

                ParticipantIntermediate(
                    participant = participant,
                    itemSubtotalMinor = itemSubtotalMinor,
                    serviceMinor = pServiceMinor,
                    taxMinor = pTaxMinor,
                    discountMinor = pDiscountMinor,
                    roundedTotalMinor = pRoundedTotalMinor,
                )
            }
        }

        val sumOfRoundedTotals = intermediates.sumOf { it.roundedTotalMinor }
        val roundingRemainder = totalMinor - sumOfRoundedTotals

        val participantSplits = intermediates.map { intermediate ->
            val isPayer = intermediate.participant.id == payer.id
            val adjustmentMinor = if (isPayer) roundingRemainder else 0L
            val finalTotalMinor = intermediate.roundedTotalMinor + adjustmentMinor
            val finalTotal = Money(finalTotalMinor, currency)
            val owesPayer = if (isPayer) Money.zero(currency) else finalTotal

            ParticipantSplit(
                participant = intermediate.participant,
                itemSubtotal = Money(intermediate.itemSubtotalMinor, currency),
                serviceShare = Money(intermediate.serviceMinor, currency),
                taxShare = Money(intermediate.taxMinor, currency),
                discountShare = Money(intermediate.discountMinor, currency),
                finalTotal = finalTotal,
                roundingAdjustment = Money(adjustmentMinor, currency),
                isPayer = isPayer,
                owesPayer = owesPayer,
            )
        }

        return BillCalculationResult(
            subtotal = Money(subtotalMinor, currency),
            serviceAmount = Money(serviceTotalMinor, currency),
            taxAmount = Money(taxTotalMinor, currency),
            discountAmount = draft.discount,
            roundingAdjustment = Money(roundingRemainder, currency),
            total = Money(totalMinor, currency),
            payer = payer,
            participantSplits = participantSplits,
        )
    }
}

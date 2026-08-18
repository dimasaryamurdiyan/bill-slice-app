package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillValidationError
import com.dimasarya.billslice.core.model.Money

class ValidateBillDraftUseCase {

    operator fun invoke(draft: BillDraft): List<BillValidationError> {
        val errors = mutableListOf<BillValidationError>()

        if (draft.items.isEmpty()) {
            errors.add(BillValidationError.EmptyItems)
        } else {
            for (item in draft.items) {
                if (item.name.isBlank()) {
                    errors.add(BillValidationError.InvalidItem(item.id, "Item name cannot be blank"))
                }
                if (item.quantity <= 0) {
                    errors.add(BillValidationError.InvalidItem(item.id, "Quantity must be greater than zero"))
                }
                if (item.unitPrice.isNegative) {
                    errors.add(BillValidationError.InvalidItem(item.id, "Unit price cannot be negative"))
                }
            }
        }

        if (draft.participants.isEmpty()) {
            errors.add(BillValidationError.EmptyParticipants)
        } else {
            val seenNames = mutableSetOf<String>()
            for (participant in draft.participants) {
                val normalizedName = participant.name.trim().lowercase()
                if (normalizedName.isBlank()) {
                    errors.add(BillValidationError.DuplicateParticipantName("Participant name cannot be blank"))
                } else if (!seenNames.add(normalizedName)) {
                    errors.add(BillValidationError.DuplicateParticipantName(participant.name))
                }
            }
        }

        val payerId = draft.payerId
        if (payerId == null) {
            errors.add(BillValidationError.MissingPayer)
        } else if (draft.participants.none { it.id == payerId }) {
            errors.add(BillValidationError.PayerNotInParticipants(payerId))
        }

        val participantIds = draft.participants.map { it.id }.toSet()
        val itemIds = draft.items.map { it.id }.toSet()

        for (assignment in draft.assignments) {
            if (assignment.itemId !in itemIds || assignment.participantId !in participantIds) {
                errors.add(BillValidationError.InvalidAssignment(assignment.itemId, assignment.participantId))
            }
        }

        if (draft.items.isNotEmpty()) {
            val unassigned = draft.unassignedItemIds()
            if (unassigned.isNotEmpty()) {
                errors.add(BillValidationError.UnassignedItems(unassigned))
            }
        }

        if (draft.serviceRate.isNegative) {
            errors.add(BillValidationError.InvalidRate("Service rate cannot be negative"))
        }

        if (draft.taxRate.isNegative) {
            errors.add(BillValidationError.InvalidRate("Tax rate cannot be negative"))
        }

        if (draft.discount.isNegative) {
            errors.add(BillValidationError.InvalidAmount("Discount cannot be negative"))
        }

        val subtotal = draft.subtotal
        if (subtotal.isZero && (!draft.serviceRate.isZero || !draft.taxRate.isZero || draft.discount.isPositive)) {
            errors.add(BillValidationError.ZeroAllocatableSubtotalWithCharges)
        }

        return errors
    }
}

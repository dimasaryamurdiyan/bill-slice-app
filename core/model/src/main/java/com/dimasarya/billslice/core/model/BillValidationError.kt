package com.dimasarya.billslice.core.model

sealed interface BillValidationError {
    data object EmptyItems : BillValidationError
    data class InvalidItem(val itemId: String, val reason: String) : BillValidationError
    data object EmptyParticipants : BillValidationError
    data class DuplicateParticipantName(val name: String) : BillValidationError
    data object MissingPayer : BillValidationError
    data class PayerNotInParticipants(val payerId: String) : BillValidationError
    data class UnassignedItems(val itemIds: List<String>) : BillValidationError
    data class InvalidAssignment(val itemId: String, val participantId: String) : BillValidationError
    data object NegativePayableTotal : BillValidationError
    data object ZeroAllocatableSubtotalWithCharges : BillValidationError
    data class InvalidRate(val message: String) : BillValidationError
    data class InvalidAmount(val message: String) : BillValidationError
}

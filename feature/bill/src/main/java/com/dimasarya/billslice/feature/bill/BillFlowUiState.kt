package com.dimasarya.billslice.feature.bill

import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillValidationError
import com.dimasarya.billslice.core.model.ReceiptTotalsValidationResult

data class BillFlowUiState(
    val step: BillFlowStep = BillFlowStep.ManualEntry,
    val draft: BillDraft = BillDraft(id = "draft-" + System.currentTimeMillis()),
    val calculationResult: BillCalculationResult? = null,
    val receiptValidationResult: ReceiptTotalsValidationResult? = null,
    val shareText: String? = null,
    val validationErrors: List<BillValidationError> = emptyList(),
    val isCalculating: Boolean = false,
    val userFeedbackMessage: String? = null,
) {
    val canProceedFromManualEntry: Boolean
        get() = draft.items.isNotEmpty() && draft.items.all { it.name.isNotBlank() && it.quantity > 0 }

    val canProceedFromAddPeople: Boolean
        get() = draft.participants.isNotEmpty() && draft.payerId != null && draft.participants.any { it.id == draft.payerId }

    val canCalculate: Boolean
        get() = draft.items.isNotEmpty() && draft.participants.isNotEmpty() && draft.payerId != null && draft.unassignedItemIds().isEmpty()
}

package com.dimasarya.billslice.feature.bill

import com.dimasarya.billslice.core.model.Rate

sealed interface BillFlowUiEvent {
    data class UpdateMerchantName(val name: String) : BillFlowUiEvent
    data class AddItem(val name: String, val unitPrice: Long, val quantity: Int) : BillFlowUiEvent
    data class UpdateItem(val itemId: String, val name: String, val unitPrice: Long, val quantity: Int) : BillFlowUiEvent
    data class DeleteItem(val itemId: String) : BillFlowUiEvent
    data class UpdateServiceRate(val rate: Rate) : BillFlowUiEvent
    data class UpdateTaxRate(val rate: Rate) : BillFlowUiEvent
    data class UpdateDiscount(val amount: Long) : BillFlowUiEvent
    data class UpdateReceiptTotal(val amount: Long?) : BillFlowUiEvent

    data class AddParticipant(val name: String) : BillFlowUiEvent
    data class RemoveParticipant(val participantId: String) : BillFlowUiEvent
    data class SelectPayer(val participantId: String) : BillFlowUiEvent

    data class AssignItem(val itemId: String, val participantId: String) : BillFlowUiEvent

    data class NavigateToStep(val step: BillFlowStep) : BillFlowUiEvent
    data object CalculateSplit : BillFlowUiEvent
    data object ClearFeedbackMessage : BillFlowUiEvent
    data class SetFeedbackMessage(val message: String) : BillFlowUiEvent
}

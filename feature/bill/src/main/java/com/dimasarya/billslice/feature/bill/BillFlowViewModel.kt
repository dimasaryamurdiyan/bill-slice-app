package com.dimasarya.billslice.feature.bill

import androidx.lifecycle.ViewModel
import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.GenerateShareTextUseCase
import com.dimasarya.billslice.core.domain.ValidateBillDraftUseCase
import com.dimasarya.billslice.core.domain.ValidateReceiptTotalsUseCase
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.ItemAssignment
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class BillFlowViewModel(
    private val validateBillDraftUseCase: ValidateBillDraftUseCase = ValidateBillDraftUseCase(),
    private val calculateBillSplitUseCase: CalculateBillSplitUseCase = CalculateBillSplitUseCase(),
    private val validateReceiptTotalsUseCase: ValidateReceiptTotalsUseCase = ValidateReceiptTotalsUseCase(),
    private val generateShareTextUseCase: GenerateShareTextUseCase = GenerateShareTextUseCase(),
    initialDraft: BillDraft = BillDraft(id = UUID.randomUUID().toString()),
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BillFlowUiState(
            step = BillFlowStep.ManualEntry,
            draft = initialDraft,
        ),
    )
    val uiState: StateFlow<BillFlowUiState> = _uiState.asStateFlow()

    fun onEvent(event: BillFlowUiEvent) {
        when (event) {
            is BillFlowUiEvent.UpdateMerchantName -> updateDraft { it.copy(merchantName = event.name) }
            is BillFlowUiEvent.AddItem -> {
                val newItem = BillItem(
                    id = UUID.randomUUID().toString(),
                    name = event.name.trim(),
                    unitPrice = Money(event.unitPrice, _uiState.value.draft.currency),
                    quantity = event.quantity.coerceAtLeast(1),
                )
                updateDraft { it.copy(items = it.items + newItem) }
            }
            is BillFlowUiEvent.UpdateItem -> {
                updateDraft { draft ->
                    val updatedItems = draft.items.map {
                        if (it.id == event.itemId) {
                            it.copy(
                                name = event.name.trim(),
                                unitPrice = Money(event.unitPrice, draft.currency),
                                quantity = event.quantity.coerceAtLeast(1),
                            )
                        } else {
                            it
                        }
                    }
                    draft.copy(items = updatedItems)
                }
            }
            is BillFlowUiEvent.DeleteItem -> {
                updateDraft { draft ->
                    draft.copy(
                        items = draft.items.filterNot { it.id == event.itemId },
                        assignments = draft.assignments.filterNot { it.itemId == event.itemId },
                    )
                }
            }
            is BillFlowUiEvent.UpdateServiceRate -> updateDraft { it.copy(serviceRate = event.rate) }
            is BillFlowUiEvent.UpdateTaxRate -> updateDraft { it.copy(taxRate = event.rate) }
            is BillFlowUiEvent.UpdateDiscount -> updateDraft {
                it.copy(discount = Money(event.amount, it.currency))
            }
            is BillFlowUiEvent.UpdateReceiptTotal -> updateDraft {
                it.copy(receiptTotal = event.amount?.let { amt -> Money(amt, it.currency) })
            }
            is BillFlowUiEvent.AddParticipant -> {
                val name = event.name.trim()
                if (name.isNotBlank()) {
                    val newParticipant = Participant(
                        id = UUID.randomUUID().toString(),
                        name = name,
                    )
                    updateDraft { draft ->
                        val updatedParticipants = draft.participants + newParticipant
                        val updatedPayer = draft.payerId ?: newParticipant.id
                        draft.copy(
                            participants = updatedParticipants,
                            payerId = updatedPayer,
                        )
                    }
                }
            }
            is BillFlowUiEvent.RemoveParticipant -> {
                updateDraft { draft ->
                    val updatedParticipants = draft.participants.filterNot { it.id == event.participantId }
                    val updatedAssignments = draft.assignments.filterNot { it.participantId == event.participantId }
                    val updatedPayer = if (draft.payerId == event.participantId) {
                        updatedParticipants.firstOrNull()?.id
                    } else {
                        draft.payerId
                    }
                    draft.copy(
                        participants = updatedParticipants,
                        assignments = updatedAssignments,
                        payerId = updatedPayer,
                    )
                }
            }
            is BillFlowUiEvent.SelectPayer -> {
                updateDraft { it.copy(payerId = event.participantId) }
            }
            is BillFlowUiEvent.AssignItem -> {
                updateDraft { draft ->
                    val filteredAssignments = draft.assignments.filterNot { it.itemId == event.itemId }
                    val newAssignment = ItemAssignment(itemId = event.itemId, participantId = event.participantId)
                    draft.copy(assignments = filteredAssignments + newAssignment)
                }
            }
            is BillFlowUiEvent.NavigateToStep -> {
                _uiState.update { it.copy(step = event.step) }
            }
            is BillFlowUiEvent.CalculateSplit -> {
                calculateSplit()
            }
            is BillFlowUiEvent.ClearFeedbackMessage -> {
                _uiState.update { it.copy(userFeedbackMessage = null) }
            }
            is BillFlowUiEvent.SetFeedbackMessage -> {
                _uiState.update { it.copy(userFeedbackMessage = event.message) }
            }
        }
    }

    private fun updateDraft(transform: (BillDraft) -> BillDraft) {
        _uiState.update { currentState ->
            val updatedDraft = transform(currentState.draft)
            val errors = validateBillDraftUseCase(updatedDraft)
            currentState.copy(
                draft = updatedDraft,
                calculationResult = null,
                receiptValidationResult = null,
                shareText = null,
                validationErrors = errors,
            )
        }
    }

    private fun calculateSplit() {
        val currentDraft = _uiState.value.draft
        val errors = validateBillDraftUseCase(currentDraft)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = errors) }
            return
        }

        try {
            val calculationResult = calculateBillSplitUseCase(currentDraft)
            val receiptValidation = validateReceiptTotalsUseCase(
                calculatedTotal = calculationResult.total,
                receiptTotal = currentDraft.receiptTotal,
            )
            val shareText = generateShareTextUseCase(
                result = calculationResult,
                merchantName = currentDraft.merchantName,
            )

            _uiState.update {
                it.copy(
                    calculationResult = calculationResult,
                    receiptValidationResult = receiptValidation,
                    shareText = shareText,
                    validationErrors = emptyList(),
                    step = BillFlowStep.CalculationSummary,
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    userFeedbackMessage = e.message ?: "Failed to calculate bill",
                )
            }
        }
    }
}

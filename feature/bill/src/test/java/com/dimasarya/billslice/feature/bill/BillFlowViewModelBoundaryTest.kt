package com.dimasarya.billslice.feature.bill

import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.GenerateShareTextUseCase
import com.dimasarya.billslice.core.domain.ValidateBillDraftUseCase
import com.dimasarya.billslice.core.domain.ValidateReceiptTotalsUseCase
import com.dimasarya.billslice.core.model.BillValidationError
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Rate
import com.dimasarya.billslice.core.model.ReceiptTotalStatus
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BillFlowViewModelBoundaryTest {

    private val validateBillDraftUseCase = ValidateBillDraftUseCase()
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase(validateBillDraftUseCase)
    private val validateReceiptTotalsUseCase = ValidateReceiptTotalsUseCase()
    private val generateShareTextUseCase = GenerateShareTextUseCase()

    private fun createViewModel(): BillFlowViewModel {
        return BillFlowViewModel(
            validateBillDraftUseCase = validateBillDraftUseCase,
            calculateBillSplitUseCase = calculateBillSplitUseCase,
            validateReceiptTotalsUseCase = validateReceiptTotalsUseCase,
            generateShareTextUseCase = generateShareTextUseCase,
        )
    }

    @Test
    fun `cannot calculate when items are unassigned`() {
        val viewModel = createViewModel()
        viewModel.onEvent(BillFlowUiEvent.AddItem("Steak", 100_000, 1))
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Alice"))
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Bob"))

        assertFalse(viewModel.uiState.value.canCalculate)

        // Attempting calculation should not calculate and leave calculationResult null
        viewModel.onEvent(BillFlowUiEvent.CalculateSplit)
        assertNull(viewModel.uiState.value.calculationResult)
        assertTrue(viewModel.uiState.value.validationErrors.any { it is BillValidationError.UnassignedItems })
    }

    @Test
    fun `receipt total mismatch produces warning status without blocking calculation`() {
        val canonicalDraft = CanonicalBillFixtures.createCanonicalDraft().copy(
            receiptTotal = Money.idr(250_000), // Mismatch with 219_450
        )
        val viewModel = BillFlowViewModel(
            validateBillDraftUseCase = validateBillDraftUseCase,
            calculateBillSplitUseCase = calculateBillSplitUseCase,
            validateReceiptTotalsUseCase = validateReceiptTotalsUseCase,
            generateShareTextUseCase = generateShareTextUseCase,
            initialDraft = canonicalDraft,
        )

        viewModel.onEvent(BillFlowUiEvent.CalculateSplit)

        assertNotNull(viewModel.uiState.value.calculationResult)
        val receiptValidation = viewModel.uiState.value.receiptValidationResult
        assertNotNull(receiptValidation)
        assertEquals(ReceiptTotalStatus.NEEDS_REVIEW, receiptValidation?.status)
        assertEquals(Money.idr(30_550), receiptValidation?.difference)
    }

    @Test
    fun `large bill amount calculations remain exact`() {
        val viewModel = createViewModel()
        // 1.5 billion IDR banquet
        viewModel.onEvent(BillFlowUiEvent.AddItem("Banquet", 1_500_000_000L, 1))
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Boss"))
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Guest"))
        val boss = viewModel.uiState.value.draft.participants.first { it.name == "Boss" }
        viewModel.onEvent(BillFlowUiEvent.SelectPayer(boss.id))
        val item = viewModel.uiState.value.draft.items.first()
        viewModel.onEvent(BillFlowUiEvent.AssignItem(item.id, boss.id))
        viewModel.onEvent(BillFlowUiEvent.UpdateTaxRate(Rate.fromPercentage(11)))
        viewModel.onEvent(BillFlowUiEvent.UpdateServiceRate(Rate.fromPercentage(10)))

        viewModel.onEvent(BillFlowUiEvent.CalculateSplit)

        val result = viewModel.uiState.value.calculationResult
        assertNotNull(result)
        // Subtotal = 1.5B, Service 10% = 150M, Tax 11% on 1.65B = 181.5M, Total = 1.8315B
        assertEquals(Money.idr(1_831_500_000L), result?.total)
    }

    @Test
    fun `modifying an item resets calculation result and share text`() {
        val canonicalDraft = CanonicalBillFixtures.createCanonicalDraft()
        val viewModel = BillFlowViewModel(
            validateBillDraftUseCase = validateBillDraftUseCase,
            calculateBillSplitUseCase = calculateBillSplitUseCase,
            validateReceiptTotalsUseCase = validateReceiptTotalsUseCase,
            generateShareTextUseCase = generateShareTextUseCase,
            initialDraft = canonicalDraft,
        )

        viewModel.onEvent(BillFlowUiEvent.CalculateSplit)
        assertNotNull(viewModel.uiState.value.calculationResult)
        assertNotNull(viewModel.uiState.value.shareText)

        // Modify item price
        val item = canonicalDraft.items.first()
        viewModel.onEvent(BillFlowUiEvent.UpdateItem(item.id, item.name, 50_000, item.quantity))

        assertNull(viewModel.uiState.value.calculationResult)
        assertNull(viewModel.uiState.value.shareText)
    }

    @Test
    fun `removing participant removes their assignments and reassigns payer if needed`() {
        val canonicalDraft = CanonicalBillFixtures.createCanonicalDraft()
        val viewModel = BillFlowViewModel(
            validateBillDraftUseCase = validateBillDraftUseCase,
            calculateBillSplitUseCase = calculateBillSplitUseCase,
            validateReceiptTotalsUseCase = validateReceiptTotalsUseCase,
            generateShareTextUseCase = generateShareTextUseCase,
            initialDraft = canonicalDraft,
        )

        val dimas = canonicalDraft.participants.first { it.name == "Dimas" }
        assertEquals(dimas.id, viewModel.uiState.value.draft.payerId)

        // Remove Dimas (who was payer)
        viewModel.onEvent(BillFlowUiEvent.RemoveParticipant(dimas.id))

        assertEquals(2, viewModel.uiState.value.draft.participants.size)
        // Payer automatically falls back to the next available participant
        assertNotNull(viewModel.uiState.value.draft.payerId)
        // Dimas's assigned item is now unassigned
        assertFalse(viewModel.uiState.value.canCalculate)
    }
}

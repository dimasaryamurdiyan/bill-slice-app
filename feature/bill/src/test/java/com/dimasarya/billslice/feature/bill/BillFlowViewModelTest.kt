package com.dimasarya.billslice.feature.bill

import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.GenerateShareTextUseCase
import com.dimasarya.billslice.core.domain.ValidateBillDraftUseCase
import com.dimasarya.billslice.core.domain.ValidateReceiptTotalsUseCase
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Rate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BillFlowViewModelTest {

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
    fun `full canonical manual bill splitting flow succeeds`() {
        val viewModel = createViewModel()

        // 1. Manual Entry
        viewModel.onEvent(BillFlowUiEvent.UpdateMerchantName("Warung Kopi"))
        viewModel.onEvent(BillFlowUiEvent.AddItem(name = "Nasi Goreng", unitPrice = 40_000, quantity = 1))
        viewModel.onEvent(BillFlowUiEvent.AddItem(name = "Chicken Steak", unitPrice = 60_000, quantity = 1))
        viewModel.onEvent(BillFlowUiEvent.AddItem(name = "Pizza", unitPrice = 90_000, quantity = 1))
        viewModel.onEvent(BillFlowUiEvent.UpdateServiceRate(Rate.fromPercentage(5)))
        viewModel.onEvent(BillFlowUiEvent.UpdateTaxRate(Rate.fromPercentage(10)))
        viewModel.onEvent(BillFlowUiEvent.UpdateDiscount(0))
        viewModel.onEvent(BillFlowUiEvent.UpdateReceiptTotal(219_450))

        assertEquals(Money.idr(190_000), viewModel.uiState.value.draft.subtotal)
        assertEquals(3, viewModel.uiState.value.draft.items.size)

        // Navigate to Add People
        viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.AddPeople))
        assertEquals(BillFlowStep.AddPeople, viewModel.uiState.value.step)

        // 2. Add People
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Dimas"))
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Arya"))
        viewModel.onEvent(BillFlowUiEvent.AddParticipant("Budi"))

        val dimas = viewModel.uiState.value.draft.participants.first { it.name == "Dimas" }
        val arya = viewModel.uiState.value.draft.participants.first { it.name == "Arya" }
        val budi = viewModel.uiState.value.draft.participants.first { it.name == "Budi" }

        // Select Dimas as payer
        viewModel.onEvent(BillFlowUiEvent.SelectPayer(dimas.id))
        assertEquals(dimas.id, viewModel.uiState.value.draft.payerId)

        // Navigate to Assign Items
        viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.AssignItems))
        assertEquals(BillFlowStep.AssignItems, viewModel.uiState.value.step)

        // 3. Assign Items
        val items = viewModel.uiState.value.draft.items
        viewModel.onEvent(BillFlowUiEvent.AssignItem(itemId = items[0].id, participantId = dimas.id))
        viewModel.onEvent(BillFlowUiEvent.AssignItem(itemId = items[1].id, participantId = arya.id))
        viewModel.onEvent(BillFlowUiEvent.AssignItem(itemId = items[2].id, participantId = budi.id))

        assertTrue(viewModel.uiState.value.canCalculate)

        // 4. Calculate
        viewModel.onEvent(BillFlowUiEvent.CalculateSplit)

        val result = viewModel.uiState.value.calculationResult
        assertNotNull(result)
        assertEquals(BillFlowStep.CalculationSummary, viewModel.uiState.value.step)
        assertEquals(Money.idr(219_450), result?.total)

        val dimasSplit = result?.participantSplits?.first { it.participant.id == dimas.id }
        val aryaSplit = result?.participantSplits?.first { it.participant.id == arya.id }
        val budiSplit = result?.participantSplits?.first { it.participant.id == budi.id }

        assertEquals(Money.idr(46_200), dimasSplit?.finalTotal)
        assertEquals(Money.idr(69_300), aryaSplit?.finalTotal)
        assertEquals(Money.idr(103_950), budiSplit?.finalTotal)

        // 5. Invalidation on draft edit
        viewModel.onEvent(BillFlowUiEvent.UpdateDiscount(1_000))
        assertNull(viewModel.uiState.value.calculationResult)
    }
}

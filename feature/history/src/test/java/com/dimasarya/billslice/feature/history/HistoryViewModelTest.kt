package com.dimasarya.billslice.feature.history

import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.EntitlementStatus
import com.dimasarya.billslice.core.domain.ObserveRecentBillsUseCase
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import com.dimasarya.billslice.core.testing.FakeBillRepository
import com.dimasarya.billslice.core.testing.FakeEntitlementRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryViewModelTest {

    private val fakeBillRepository = FakeBillRepository()
    private val fakeEntitlementRepository = FakeEntitlementRepository(EntitlementStatus.FREE)
    private val observeRecentBillsUseCase = ObserveRecentBillsUseCase(
        billRepository = fakeBillRepository,
        entitlementRepository = fakeEntitlementRepository,
    )
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase()

    private fun createViewModel(): HistoryViewModel {
        return HistoryViewModel(
            observeRecentBillsUseCase = observeRecentBillsUseCase,
            entitlementRepository = fakeEntitlementRepository,
        )
    }

    @Test
    fun `empty repository emits HistoryUiState Empty`() = runBlocking {
        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it !is HistoryUiState.Loading }
        assertTrue(state is HistoryUiState.Empty)
    }

    @Test
    fun `populated repository emits Populated state with bills`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        fakeBillRepository.saveBill(draft, calculateBillSplitUseCase(draft))

        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it !is HistoryUiState.Loading }

        assertTrue(state is HistoryUiState.Populated)
        val populated = state as HistoryUiState.Populated
        assertEquals(1, populated.bills.size)
        assertEquals(draft.merchantName, populated.bills[0].merchantName)
        assertFalse(populated.hasOlderBills)
        assertFalse(populated.isPro)
    }

    @Test
    fun `free user with 5 or more bills sets hasOlderBills true`() = runBlocking {
        for (i in 1..6) {
            val draft = BillDraft(
                id = "bill-$i",
                merchantName = "Warung $i",
                currency = CurrencyCode.IDR,
                items = CanonicalBillFixtures.createCanonicalDraft().items,
                participants = CanonicalBillFixtures.createCanonicalDraft().participants,
                assignments = CanonicalBillFixtures.createCanonicalDraft().assignments,
                payerId = CanonicalBillFixtures.participantDimas.id,
                createdAtEpochMillis = 1000L * i,
            )
            fakeBillRepository.saveBill(draft, calculateBillSplitUseCase(draft))
        }

        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it !is HistoryUiState.Loading }

        assertTrue(state is HistoryUiState.Populated)
        val populated = state as HistoryUiState.Populated
        assertEquals(5, populated.bills.size)
        assertTrue(populated.hasOlderBills)
        assertFalse(populated.isPro)
    }

    @Test
    fun `pro user exposes all bills with hasOlderBills false`() = runBlocking {
        fakeEntitlementRepository.setStatus(EntitlementStatus.PRO)

        for (i in 1..6) {
            val draft = BillDraft(
                id = "bill-$i",
                merchantName = "Warung $i",
                currency = CurrencyCode.IDR,
                items = CanonicalBillFixtures.createCanonicalDraft().items,
                participants = CanonicalBillFixtures.createCanonicalDraft().participants,
                assignments = CanonicalBillFixtures.createCanonicalDraft().assignments,
                payerId = CanonicalBillFixtures.participantDimas.id,
                createdAtEpochMillis = 1000L * i,
            )
            fakeBillRepository.saveBill(draft, calculateBillSplitUseCase(draft))
        }

        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it !is HistoryUiState.Loading }

        assertTrue(state is HistoryUiState.Populated)
        val populated = state as HistoryUiState.Populated
        assertEquals(6, populated.bills.size)
        assertFalse(populated.hasOlderBills)
        assertTrue(populated.isPro)
    }
}

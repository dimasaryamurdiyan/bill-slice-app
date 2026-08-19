package com.dimasarya.billslice.feature.home

import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.ObserveRecentBillsUseCase
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import com.dimasarya.billslice.core.testing.FakeBillRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    private val fakeBillRepository = FakeBillRepository()
    private val observeRecentBillsUseCase = ObserveRecentBillsUseCase(fakeBillRepository)
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase()

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(observeRecentBillsUseCase)
    }

    @Test
    fun `empty repository results in empty recentBills list`() = runBlocking {
        val viewModel = createViewModel()
        val state = viewModel.uiState.first()

        assertTrue(state.recentBills.isEmpty())
        assertTrue(state.isOfflineReady)
    }

    @Test
    fun `populated repository limits recent bills on Home to 3`() = runBlocking {
        for (i in 1..5) {
            val draft = BillDraft(
                id = "bill-$i",
                merchantName = "Cafe $i",
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
        val state = viewModel.uiState.first { it.recentBills.isNotEmpty() }

        assertEquals(3, state.recentBills.size)
        assertEquals("Cafe 5", state.recentBills[0].merchantName)
        assertEquals("Cafe 4", state.recentBills[1].merchantName)
        assertEquals("Cafe 3", state.recentBills[2].merchantName)
    }
}

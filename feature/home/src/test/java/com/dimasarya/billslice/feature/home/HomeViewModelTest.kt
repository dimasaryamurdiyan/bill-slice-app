package com.dimasarya.billslice.feature.home

import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.BillRepository
import com.dimasarya.billslice.core.domain.ObserveRecentBillsUseCase
import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.RecentBillSummary
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import com.dimasarya.billslice.core.testing.FakeBillRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
        val state = viewModel.uiState.first { it.recentBills !is RecentBillsUiState.Loading }

        assertEquals(RecentBillsUiState.Empty, state.recentBills)
        assertTrue(state.isOfflineReady)
    }

    @Test
    fun `populated repository limits recent bills on Home to 5`() = runBlocking {
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
        val state = viewModel.uiState.first { it.recentBills is RecentBillsUiState.Populated }
        val recentBills = (state.recentBills as RecentBillsUiState.Populated).bills

        assertEquals(5, recentBills.size)
        assertEquals("Cafe 5", recentBills[0].merchantName)
        assertEquals("Cafe 1", recentBills[4].merchantName)
    }

    @Test
    fun `repository observation failure makes recent bills unavailable`() = runBlocking {
        var reportedFailure: Throwable? = null
        val failingRepository = object : BillRepository {
            override suspend fun saveBill(
                draft: BillDraft,
                calculationResult: BillCalculationResult,
            ): Result<Unit> = Result.success(Unit)

            override fun observeBills() = flow<List<RecentBillSummary>> {
                throw IllegalStateException("Simulated read error")
            }

            override suspend fun getBill(id: String): Result<BillDraft> =
                Result.failure(NoSuchElementException(id))
        }
        val viewModel = HomeViewModel(
            observeRecentBillsUseCase = ObserveRecentBillsUseCase(failingRepository),
            onRecentBillsFailure = { reportedFailure = it },
        )

        val state = viewModel.uiState.first { it.recentBills !is RecentBillsUiState.Loading }

        assertEquals(RecentBillsUiState.Unavailable, state.recentBills)
        assertEquals("Simulated read error", reportedFailure?.message)
    }

    @Test
    fun `recent bill date follows the requested locale`() {
        val utc = TimeZone.getTimeZone("UTC")
        val epochMillis = Calendar.getInstance(utc, Locale.US).run {
            clear()
            set(2026, Calendar.AUGUST, 13, 12, 0)
            timeInMillis
        }

        val result = formatRecentBillDate(epochMillis, Locale.US, utc)

        assertEquals("Aug 13, 2026", result)
    }
}

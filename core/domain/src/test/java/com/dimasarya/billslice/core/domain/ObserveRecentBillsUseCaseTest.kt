package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import com.dimasarya.billslice.core.testing.FakeBillRepository
import com.dimasarya.billslice.core.testing.FakeEntitlementRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveRecentBillsUseCaseTest {

    private val fakeRepository = FakeBillRepository()
    private val fakeEntitlementRepository = FakeEntitlementRepository(EntitlementStatus.FREE)
    private val observeRecentBillsUseCase = ObserveRecentBillsUseCase(
        billRepository = fakeRepository,
        entitlementRepository = fakeEntitlementRepository,
    )
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase()

    @Test
    fun `free user observes up to 5 newest bills in descending order`() = runBlocking {
        // Save 7 bills with distinct timestamps
        for (i in 1..7) {
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
            fakeRepository.saveBill(draft, calculateBillSplitUseCase(draft))
        }

        val observed = observeRecentBillsUseCase().first()

        assertEquals(5, observed.size)
        // Newest bills are 7, 6, 5, 4, 3
        assertEquals("bill-7", observed[0].id)
        assertEquals("bill-6", observed[1].id)
        assertEquals("bill-5", observed[2].id)
        assertEquals("bill-4", observed[3].id)
        assertEquals("bill-3", observed[4].id)
    }

    @Test
    fun `pro user observes all retained bills`() = runBlocking {
        fakeEntitlementRepository.setStatus(EntitlementStatus.PRO)

        for (i in 1..8) {
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
            fakeRepository.saveBill(draft, calculateBillSplitUseCase(draft))
        }

        val observed = observeRecentBillsUseCase().first()

        assertEquals(8, observed.size)
        assertEquals("bill-8", observed[0].id)
        assertEquals("bill-1", observed[7].id)
    }

    @Test
    fun `empty repository emits empty list`() = runBlocking {
        val observed = observeRecentBillsUseCase().first()
        assertEquals(0, observed.size)
    }
}

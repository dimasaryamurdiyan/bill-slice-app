package com.dimasarya.billslice.core.data.repository

import com.dimasarya.billslice.core.database.dao.BillDao
import com.dimasarya.billslice.core.database.model.BillEntity
import com.dimasarya.billslice.core.database.model.BillItemEntity
import com.dimasarya.billslice.core.database.model.BillWithDetails
import com.dimasarya.billslice.core.database.model.ItemAssignmentEntity
import com.dimasarya.billslice.core.database.model.ParticipantEntity
import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BillRepositoryImplTest {

    private val fakeDao = FakeBillDao()
    private val repository = BillRepositoryImpl(fakeDao)
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase()

    @Test
    fun `saveBill and getBill performs complete round trip preserving all editable fields`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calculationResult = calculateBillSplitUseCase(draft)

        val saveResult = repository.saveBill(draft, calculationResult)
        assertTrue(saveResult.isSuccess)

        val fetchedResult = repository.getBill(draft.id)
        assertTrue(fetchedResult.isSuccess)
        val fetchedDraft = fetchedResult.getOrNull()!!

        assertEquals(draft.id, fetchedDraft.id)
        assertEquals(draft.merchantName, fetchedDraft.merchantName)
        assertEquals(draft.currency, fetchedDraft.currency)
        assertEquals(draft.items.size, fetchedDraft.items.size)
        assertEquals(draft.items[0].name, fetchedDraft.items[0].name)
        assertEquals(draft.items[0].unitPrice, fetchedDraft.items[0].unitPrice)
        assertEquals(draft.items[0].quantity, fetchedDraft.items[0].quantity)
        assertEquals(draft.participants.size, fetchedDraft.participants.size)
        assertEquals(draft.assignments.size, fetchedDraft.assignments.size)
        assertEquals(draft.payerId, fetchedDraft.payerId)
        assertEquals(draft.serviceRate, fetchedDraft.serviceRate)
        assertEquals(draft.taxRate, fetchedDraft.taxRate)
        assertEquals(draft.discount, fetchedDraft.discount)
        assertEquals(draft.receiptTotal, fetchedDraft.receiptTotal)
    }

    @Test
    fun `saving existing bill ID updates in-place without duplicating`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calc = calculateBillSplitUseCase(draft)
        repository.saveBill(draft, calc)

        val updatedDraft = draft.copy(merchantName = "Updated Cafe")
        val updateResult = repository.saveBill(updatedDraft, calc)
        assertTrue(updateResult.isSuccess)

        val fetched = repository.getBill(draft.id).getOrNull()!!
        assertEquals("Updated Cafe", fetched.merchantName)
        assertEquals(1, fakeDao.storedBills.size)
    }

    @Test
    fun `observeBills streams recent summaries in descending creation order`() = runBlocking {
        for (i in 1..3) {
            val draft = CanonicalBillFixtures.createCanonicalDraft().copy(
                id = "bill-$i",
                merchantName = "Cafe $i",
                createdAtEpochMillis = 1000L * i,
            )
            repository.saveBill(draft, calculateBillSplitUseCase(draft))
        }

        val summaries = repository.observeBills().first()
        assertEquals(3, summaries.size)
        assertEquals("bill-3", summaries[0].id)
        assertEquals("bill-2", summaries[1].id)
        assertEquals("bill-1", summaries[2].id)
    }

    @Test
    fun `getBill returns failure when bill ID is missing`() = runBlocking {
        val result = repository.getBill("non-existent")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoSuchElementException)
    }

    @Test
    fun `saveBill returns failure when DAO throws exception`() = runBlocking {
        fakeDao.shouldThrow = true
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calc = calculateBillSplitUseCase(draft)

        val result = repository.saveBill(draft, calc)
        assertTrue(result.isFailure)
    }
}

private class FakeBillDao : BillDao {
    val storedBills = mutableMapOf<String, BillWithDetails>()
    private val flow = MutableStateFlow<List<BillWithDetails>>(emptyList())
    var shouldThrow = false

    override fun observeBillsWithDetails(): Flow<List<BillWithDetails>> {
        return flow.map { it.sortedByDescending { b -> b.bill.createdAtEpochMillis } }
    }

    override suspend fun getBillWithDetails(id: String): BillWithDetails? {
        if (shouldThrow) throw IllegalStateException("Database error")
        return storedBills[id]
    }

    override suspend fun insertOrUpdateBill(bill: BillEntity) {}
    override suspend fun insertItems(items: List<BillItemEntity>) {}
    override suspend fun insertParticipants(participants: List<ParticipantEntity>) {}
    override suspend fun insertAssignments(assignments: List<ItemAssignmentEntity>) {}
    override suspend fun deleteItemsForBill(billId: String) {}
    override suspend fun deleteParticipantsForBill(billId: String) {}
    override suspend fun deleteAssignmentsForBill(billId: String) {}

    override suspend fun upsertCompleteBill(
        bill: BillEntity,
        items: List<BillItemEntity>,
        participants: List<ParticipantEntity>,
        assignments: List<ItemAssignmentEntity>,
    ) {
        if (shouldThrow) throw IllegalStateException("Disk full")
        val details = BillWithDetails(
            bill = bill,
            items = items,
            participants = participants,
            assignments = assignments,
        )
        storedBills[bill.id] = details
        flow.value = storedBills.values.toList()
    }
}

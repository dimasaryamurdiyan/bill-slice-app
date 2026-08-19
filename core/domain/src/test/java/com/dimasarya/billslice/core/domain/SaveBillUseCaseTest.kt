package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import com.dimasarya.billslice.core.testing.FakeBillRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveBillUseCaseTest {

    private val fakeRepository = FakeBillRepository()
    private val saveBillUseCase = SaveBillUseCase(fakeRepository)
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase()

    @Test
    fun `saveBill successfully stores canonical bill`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calculationResult = calculateBillSplitUseCase(draft)

        val result = saveBillUseCase(draft, calculationResult)

        assertTrue(result.isSuccess)
        val fetched = fakeRepository.getBill(draft.id)
        assertTrue(fetched.isSuccess)
        assertEquals(draft, fetched.getOrNull())
    }

    @Test
    fun `saveBill returns typed failure on repository error`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calculationResult = calculateBillSplitUseCase(draft)
        fakeRepository.shouldFailSave = true

        val result = saveBillUseCase(draft, calculationResult)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `saveBill with existing ID updates in place without duplicating`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calculationResult = calculateBillSplitUseCase(draft)
        saveBillUseCase(draft, calculationResult)

        val updatedDraft = draft.copy(merchantName = "Updated Warung")
        val updateResult = saveBillUseCase(updatedDraft, calculationResult)

        assertTrue(updateResult.isSuccess)
        val fetched = fakeRepository.getBill(draft.id)
        assertEquals("Updated Warung", fetched.getOrNull()?.merchantName)
    }
}

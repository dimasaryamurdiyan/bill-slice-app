package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import com.dimasarya.billslice.core.testing.FakeBillRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetBillUseCaseTest {

    private val fakeRepository = FakeBillRepository()
    private val getBillUseCase = GetBillUseCase(fakeRepository)
    private val calculateBillSplitUseCase = CalculateBillSplitUseCase()

    @Test
    fun `getBill returns draft when bill exists`() = runBlocking {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val calculation = calculateBillSplitUseCase(draft)
        fakeRepository.saveBill(draft, calculation)

        val result = getBillUseCase(draft.id)

        assertTrue(result.isSuccess)
        assertEquals(draft, result.getOrNull())
    }

    @Test
    fun `getBill returns failure when bill does not exist`() = runBlocking {
        val result = getBillUseCase("non-existent-id")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoSuchElementException)
    }

    @Test
    fun `getBill returns failure when read error occurs`() = runBlocking {
        fakeRepository.shouldFailGet = true

        val result = getBillUseCase("any-id")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }
}

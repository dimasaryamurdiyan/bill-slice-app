package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.ReceiptTotalStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidateReceiptTotalsUseCaseTest {

    private val useCase = ValidateReceiptTotalsUseCase()

    @Test
    fun `matching total returns LOOKS_GOOD`() {
        val total = Money.idr(219_450)
        val result = useCase(calculatedTotal = total, receiptTotal = total)

        assertEquals(ReceiptTotalStatus.LOOKS_GOOD, result.status)
        assertEquals(Money.idr(0), result.difference)
    }

    @Test
    fun `missing total returns MISSING_TOTAL`() {
        val total = Money.idr(219_450)
        val result = useCase(calculatedTotal = total, receiptTotal = null)

        assertEquals(ReceiptTotalStatus.MISSING_TOTAL, result.status)
        assertEquals(Money.idr(0), result.difference)
    }

    @Test
    fun `mismatched total returns NEEDS_REVIEW with difference`() {
        val calcTotal = Money.idr(219_450)
        val receiptTotal = Money.idr(220_000)
        val result = useCase(calculatedTotal = calcTotal, receiptTotal = receiptTotal)

        assertEquals(ReceiptTotalStatus.NEEDS_REVIEW, result.status)
        assertEquals(Money.idr(550), result.difference)
    }
}

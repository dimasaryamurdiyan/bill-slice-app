package com.dimasarya.billslice.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyTest {

    @Test
    fun `idr money arithmetic works deterministically`() {
        val m1 = Money.idr(40_000)
        val m2 = Money.idr(60_000)

        assertEquals(Money.idr(100_000), m1 + m2)
        assertEquals(Money.idr(20_000), m2 - m1)
        assertEquals(Money.idr(120_000), m1 * 3)
        assertTrue(m1 < m2)
        assertFalse(m1.isZero)
        assertTrue(Money.zero(CurrencyCode.IDR).isZero)
    }

    @Test
    fun `idr formatting matches indonesian style`() {
        val m = Money.idr(219_450)
        assertEquals("Rp219.450", m.format())

        val zero = Money.idr(0)
        assertEquals("Rp0", zero.format())

        val small = Money.idr(500)
        assertEquals("Rp500", small.format())

        val million = Money.idr(1_250_000)
        assertEquals("Rp1.250.000", million.format())
    }

    @Test
    fun `rate arithmetic produces exact basis points`() {
        val fivePercent = Rate.fromPercentage(5)
        val tenPercent = Rate.fromPercentage(10)

        assertEquals(500L, fivePercent.basisPoints)
        assertEquals(1000L, tenPercent.basisPoints)
        assertEquals("5%", fivePercent.format())
        assertEquals("10%", tenPercent.format())
    }
}

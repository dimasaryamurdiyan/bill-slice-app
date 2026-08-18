package com.dimasarya.billslice.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class RateTest {

    @Test
    fun `zero rate properties`() {
        val rate = Rate.ZERO
        assertEquals(0L, rate.basisPoints)
        assertTrue(rate.isZero)
        assertFalse(rate.isNegative)
        assertEquals(0.0, rate.percentageDouble, 0.0001)
        assertEquals("0%", rate.format())
    }

    @Test
    fun `integer percentage creation`() {
        val rate = Rate.fromPercentage(5)
        assertEquals(500L, rate.basisPoints)
        assertEquals(5.0, rate.percentageDouble, 0.0001)
        assertEquals("5%", rate.format())
    }

    @Test
    fun `double percentage creation with decimals`() {
        val rate = Rate.fromPercentage(5.5)
        assertEquals(550L, rate.basisPoints)
        assertEquals(5.5, rate.percentageDouble, 0.0001)
        assertEquals("5.5%", rate.format())
    }

    @Test
    fun `double percentage creation with two decimals`() {
        val rate = Rate.fromPercentage(10.25)
        assertEquals(1025L, rate.basisPoints)
        assertEquals(10.25, rate.percentageDouble, 0.0001)
        assertEquals("10.25%", rate.format())
    }

    @Test
    fun `big decimal percentage creation`() {
        val rate = Rate.fromPercentage(BigDecimal("11.5"))
        assertEquals(1150L, rate.basisPoints)
        assertEquals(11.5, rate.percentageDouble, 0.0001)
        assertEquals("11.5%", rate.format())
    }

    @Test
    fun `negative rate properties`() {
        val rate = Rate(-500L)
        assertTrue(rate.isNegative)
        assertFalse(rate.isZero)
        assertEquals(-5.0, rate.percentageDouble, 0.0001)
    }

    @Test
    fun `multiplier calculations`() {
        val rate = Rate.fromPercentage(10)
        assertEquals(0.10, rate.multiplierDouble, 0.0001)
        assertEquals(BigDecimal("0.10000000"), rate.multiplierBigDecimal)
    }
}

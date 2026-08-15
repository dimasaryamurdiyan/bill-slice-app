package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.core.model.ParticipantSplit
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateShareTextUseCaseTest {

    private val useCase = GenerateShareTextUseCase()

    @Test
    fun `generates correct share text with merchant and owes lines`() {
        val dimas = Participant("1", "Dimas")
        val arya = Participant("2", "Arya")
        val budi = Participant("3", "Budi")

        val result = BillCalculationResult(
            subtotal = Money.idr(190_000),
            serviceAmount = Money.idr(9_500),
            taxAmount = Money.idr(19_950),
            discountAmount = Money.idr(0),
            roundingAdjustment = Money.idr(0),
            total = Money.idr(219_450),
            payer = dimas,
            participantSplits = listOf(
                ParticipantSplit(
                    participant = dimas,
                    itemSubtotal = Money.idr(40_000),
                    serviceShare = Money.idr(2_000),
                    taxShare = Money.idr(4_200),
                    discountShare = Money.idr(0),
                    finalTotal = Money.idr(46_200),
                    roundingAdjustment = Money.idr(0),
                    isPayer = true,
                    owesPayer = Money.idr(0),
                ),
                ParticipantSplit(
                    participant = arya,
                    itemSubtotal = Money.idr(60_000),
                    serviceShare = Money.idr(3_000),
                    taxShare = Money.idr(6_300),
                    discountShare = Money.idr(0),
                    finalTotal = Money.idr(69_300),
                    roundingAdjustment = Money.idr(0),
                    isPayer = false,
                    owesPayer = Money.idr(69_300),
                ),
                ParticipantSplit(
                    participant = budi,
                    itemSubtotal = Money.idr(90_000),
                    serviceShare = Money.idr(4_500),
                    taxShare = Money.idr(9_450),
                    discountShare = Money.idr(0),
                    finalTotal = Money.idr(103_950),
                    roundingAdjustment = Money.idr(0),
                    isPayer = false,
                    owesPayer = Money.idr(103_950),
                ),
            ),
        )

        val text = useCase(result = result, merchantName = "Warung Kopi")

        assertTrue(text.contains("BillSlice result"))
        assertTrue(text.contains("Warung Kopi"))
        assertTrue(text.contains("Paid by: Dimas"))
        assertTrue(text.contains("Arya owes Dimas: Rp69.300"))
        assertTrue(text.contains("Budi owes Dimas: Rp103.950"))
        assertTrue(text.contains("Dimas's share: Rp46.200"))
        assertTrue(text.contains("Total: Rp219.450"))
        assertTrue(text.contains("Tax/service included."))
    }
}

package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ItemAssignment
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.core.model.Rate
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateBillSplitUseCaseTest {

    private val useCase = CalculateBillSplitUseCase()

    @Test
    fun `canonical fixture with one owner produces expected totals and owes lines`() {
        val draft = CanonicalBillFixtures.createCanonicalDraft()
        val dimas = CanonicalBillFixtures.participantDimas
        val arya = CanonicalBillFixtures.participantArya
        val budi = CanonicalBillFixtures.participantBudi

        val result = useCase(draft)

        assertNotNull(result)
        assertEquals(Money.idr(190_000), result.subtotal)
        assertEquals(Money.idr(9_500), result.serviceAmount)
        assertEquals(Money.idr(19_950), result.taxAmount)
        assertEquals(Money.idr(0), result.discountAmount)
        assertEquals(Money.idr(219_450), result.total)

        val dimasShare = result.participantSplits.first { it.participant.id == dimas.id }
        val aryaShare = result.participantSplits.first { it.participant.id == arya.id }
        val budiShare = result.participantSplits.first { it.participant.id == budi.id }

        assertEquals(Money.idr(46_200), dimasShare.finalTotal)
        assertEquals(Money.idr(69_300), aryaShare.finalTotal)
        assertEquals(Money.idr(103_950), budiShare.finalTotal)

        // Dimas is payer; Arya owes Dimas Rp69.300, Budi owes Dimas Rp103.950
        assertEquals(Money.idr(0), dimasShare.owesPayer)
        assertEquals(Money.idr(69_300), aryaShare.owesPayer)
        assertEquals(Money.idr(103_950), budiShare.owesPayer)
    }

    @Test
    fun `rounding remainder is assigned to payer deterministically`() {
        val alice = Participant(id = "p-1", name = "Alice")
        val bob = Participant(id = "p-2", name = "Bob")
        val charlie = Participant(id = "p-3", name = "Charlie")

        val item1 = BillItem(id = "i-1", name = "Dish 1", unitPrice = Money.idr(10_000), quantity = 1)
        val item2 = BillItem(id = "i-2", name = "Dish 2", unitPrice = Money.idr(10_000), quantity = 1)
        val item3 = BillItem(id = "i-3", name = "Dish 3", unitPrice = Money.idr(10_000), quantity = 1)

        val draft = BillDraft(
            id = "bill-rounding",
            currency = CurrencyCode.IDR,
            items = listOf(item1, item2, item3),
            participants = listOf(alice, bob, charlie),
            assignments = listOf(
                ItemAssignment(itemId = item1.id, participantId = alice.id),
                ItemAssignment(itemId = item2.id, participantId = bob.id),
                ItemAssignment(itemId = item3.id, participantId = charlie.id),
            ),
            payerId = charlie.id,
            serviceRate = Rate.fromPercentage(5),
            taxRate = Rate.fromPercentage(10),
            discount = Money.idr(100),
        )

        val result = useCase(draft)

        // Subtotal = 30000, Service = 1500, Tax = 3150, Discount = 100, Total = 34550
        assertEquals(Money.idr(34_550), result.total)

        val sumSplits = result.participantSplits.fold(Money.idr(0)) { acc, split -> acc + split.finalTotal }
        assertEquals(result.total, sumSplits)

        val charlieSplit = result.participantSplits.first { it.participant.id == charlie.id }
        assertTrue(charlieSplit.isPayer)
        assertEquals(Money.idr(0), charlieSplit.owesPayer)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invalid draft throws IllegalArgumentException`() {
        val draft = BillDraft(id = "invalid-bill")
        useCase(draft)
    }

}

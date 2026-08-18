package com.dimasarya.billslice.feature.bill

import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ItemAssignment
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.core.model.Rate

internal object SampleBillData {

    val participantDimas = Participant(id = "p-dimas", name = "Dimas")
    val participantArya = Participant(id = "p-arya", name = "Arya")
    val participantBudi = Participant(id = "p-budi", name = "Budi")

    val itemNasiGoreng = BillItem(
        id = "item-nasi-goreng",
        name = "Nasi Goreng",
        unitPrice = Money.idr(40_000),
        quantity = 1,
    )

    val itemChickenSteak = BillItem(
        id = "item-chicken-steak",
        name = "Chicken Steak",
        unitPrice = Money.idr(60_000),
        quantity = 1,
    )

    val itemPizza = BillItem(
        id = "item-pizza",
        name = "Pizza",
        unitPrice = Money.idr(90_000),
        quantity = 1,
    )

    fun createSampleDraft(): BillDraft {
        return BillDraft(
            id = "canonical-draft-1",
            merchantName = "Warung Kopi",
            currency = CurrencyCode.IDR,
            items = listOf(itemNasiGoreng, itemChickenSteak, itemPizza),
            participants = listOf(participantDimas, participantArya, participantBudi),
            assignments = listOf(
                ItemAssignment(itemId = itemNasiGoreng.id, participantId = participantDimas.id),
                ItemAssignment(itemId = itemChickenSteak.id, participantId = participantArya.id),
                ItemAssignment(itemId = itemPizza.id, participantId = participantBudi.id),
            ),
            payerId = participantDimas.id,
            serviceRate = Rate.fromPercentage(5),
            taxRate = Rate.fromPercentage(10),
            discount = Money.idr(0),
            receiptTotal = Money.idr(219_450),
        )
    }
}

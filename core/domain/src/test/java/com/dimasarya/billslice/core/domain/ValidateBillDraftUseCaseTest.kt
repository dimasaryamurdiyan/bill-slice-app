package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.BillValidationError
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ItemAssignment
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.core.model.Rate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateBillDraftUseCaseTest {

    private val validateUseCase = ValidateBillDraftUseCase()

    @Test
    fun `empty items returns EmptyItems error`() {
        val draft = BillDraft(id = "1")
        val errors = validateUseCase(draft)
        assertTrue(errors.contains(BillValidationError.EmptyItems))
    }

    @Test
    fun `item with blank name or invalid quantity returns InvalidItem`() {
        val item1 = BillItem(id = "i1", name = "  ", unitPrice = Money.idr(10_000), quantity = 1)
        val item2 = BillItem(id = "i2", name = "Tea", unitPrice = Money.idr(5_000), quantity = 0)
        val draft = BillDraft(id = "1", items = listOf(item1, item2))
        val errors = validateUseCase(draft)
        assertTrue(errors.any { it is BillValidationError.InvalidItem && it.itemId == "i1" })
        assertTrue(errors.any { it is BillValidationError.InvalidItem && it.itemId == "i2" })
    }

    @Test
    fun `empty participants returns EmptyParticipants`() {
        val item = BillItem(id = "i1", name = "Rice", unitPrice = Money.idr(10_000), quantity = 1)
        val draft = BillDraft(id = "1", items = listOf(item))
        val errors = validateUseCase(draft)
        assertTrue(errors.contains(BillValidationError.EmptyParticipants))
    }

    @Test
    fun `duplicate participant names returns DuplicateParticipantName`() {
        val item = BillItem(id = "i1", name = "Rice", unitPrice = Money.idr(10_000), quantity = 1)
        val p1 = Participant(id = "p1", name = "Dimas")
        val p2 = Participant(id = "p2", name = "dimas")
        val draft = BillDraft(id = "1", items = listOf(item), participants = listOf(p1, p2))
        val errors = validateUseCase(draft)
        assertTrue(errors.any { it is BillValidationError.DuplicateParticipantName })
    }

    @Test
    fun `missing or invalid payer returns proper error`() {
        val item = BillItem(id = "i1", name = "Rice", unitPrice = Money.idr(10_000), quantity = 1)
        val p1 = Participant(id = "p1", name = "Dimas")
        val draftNoPayer = BillDraft(id = "1", items = listOf(item), participants = listOf(p1), payerId = null)
        assertTrue(validateUseCase(draftNoPayer).contains(BillValidationError.MissingPayer))

        val draftWrongPayer = BillDraft(id = "1", items = listOf(item), participants = listOf(p1), payerId = "unknown")
        assertTrue(validateUseCase(draftWrongPayer).any { it is BillValidationError.PayerNotInParticipants })
    }

    @Test
    fun `unassigned items returns UnassignedItems error`() {
        val item1 = BillItem(id = "i1", name = "Rice", unitPrice = Money.idr(10_000), quantity = 1)
        val item2 = BillItem(id = "i2", name = "Tea", unitPrice = Money.idr(5_000), quantity = 1)
        val p1 = Participant(id = "p1", name = "Dimas")
        val draft = BillDraft(
            id = "1",
            items = listOf(item1, item2),
            participants = listOf(p1),
            payerId = p1.id,
            assignments = listOf(ItemAssignment(itemId = "i1", participantId = p1.id)),
        )
        val errors = validateUseCase(draft)
        assertTrue(errors.any { it is BillValidationError.UnassignedItems && it.itemIds == listOf("i2") })
    }

    @Test
    fun `valid canonical draft returns no errors`() {
        val item1 = BillItem(id = "i1", name = "Rice", unitPrice = Money.idr(10_000), quantity = 1)
        val p1 = Participant(id = "p1", name = "Dimas")
        val draft = BillDraft(
            id = "1",
            items = listOf(item1),
            participants = listOf(p1),
            payerId = p1.id,
            assignments = listOf(ItemAssignment(itemId = "i1", participantId = p1.id)),
            serviceRate = Rate.fromPercentage(5),
            taxRate = Rate.fromPercentage(10),
            discount = Money.idr(0),
        )
        val errors = validateUseCase(draft)
        assertTrue(errors.isEmpty())
    }
}

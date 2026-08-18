package com.dimasarya.billslice.core.model

data class BillDraft(
    val id: String,
    val merchantName: String = "",
    val currency: CurrencyCode = CurrencyCode.IDR,
    val items: List<BillItem> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val assignments: List<ItemAssignment> = emptyList(),
    val payerId: String? = null,
    val serviceRate: Rate = Rate.ZERO,
    val taxRate: Rate = Rate.ZERO,
    val discount: Money = Money.zero(currency),
    val receiptTotal: Money? = null,
) {
    val subtotal: Money
        get() = items.fold(Money.zero(currency)) { acc, item -> acc + item.subtotal }

    fun findAssignmentFor(itemId: String): ItemAssignment? {
        return assignments.firstOrNull { it.itemId == itemId }
    }

    fun isItemAssigned(itemId: String): Boolean {
        return assignments.any { it.itemId == itemId }
    }

    fun unassignedItemIds(): List<String> {
        val assignedSet = assignments.map { it.itemId }.toSet()
        return items.map { it.id }.filter { it !in assignedSet }
    }
}

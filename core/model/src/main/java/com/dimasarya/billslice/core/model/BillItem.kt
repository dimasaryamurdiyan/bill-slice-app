package com.dimasarya.billslice.core.model

data class BillItem(
    val id: String,
    val name: String,
    val unitPrice: Money,
    val quantity: Int = 1,
) {
    val subtotal: Money
        get() = unitPrice * quantity
}

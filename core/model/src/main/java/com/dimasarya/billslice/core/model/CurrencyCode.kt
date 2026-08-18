package com.dimasarya.billslice.core.model

enum class CurrencyCode(
    val code: String,
    val symbol: String,
    val decimalDigits: Int,
) {
    IDR(code = "IDR", symbol = "Rp", decimalDigits = 0),
    USD(code = "USD", symbol = "$", decimalDigits = 2),
    SGD(code = "SGD", symbol = "S$", decimalDigits = 2),
}

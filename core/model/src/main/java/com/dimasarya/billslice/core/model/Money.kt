package com.dimasarya.billslice.core.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class Money(
    val amountMinor: Long,
    val currency: CurrencyCode = CurrencyCode.IDR,
) : Comparable<Money> {

    init {
        // Amount minor represents units in minor currency units (e.g. 1 IDR = 1 unit, 1 USD cent = 1 unit)
    }

    val isZero: Boolean
        get() = amountMinor == 0L

    val isPositive: Boolean
        get() = amountMinor > 0L

    val isNegative: Boolean
        get() = amountMinor < 0L

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies: $currency and ${other.currency}" }
        return Money(amountMinor + other.amountMinor, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Cannot subtract different currencies: $currency and ${other.currency}" }
        return Money(amountMinor - other.amountMinor, currency)
    }

    operator fun times(multiplier: Int): Money {
        return Money(amountMinor * multiplier.toLong(), currency)
    }

    operator fun times(multiplier: Long): Money {
        return Money(amountMinor * multiplier, currency)
    }

    override fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Cannot compare different currencies: $currency and ${other.currency}" }
        return amountMinor.compareTo(other.amountMinor)
    }

    fun format(): String {
        return when (currency) {
            CurrencyCode.IDR -> {
                val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
                    groupingSeparator = '.'
                    decimalSeparator = ','
                }
                val formatter = DecimalFormat("#,###", symbols)
                val formattedNumber = formatter.format(amountMinor)
                "${currency.symbol}$formattedNumber"
            }
            else -> {
                val symbols = DecimalFormatSymbols(Locale.US)
                val formatter = DecimalFormat("#,##0.00", symbols)
                val formattedNumber = formatter.format(amountMinor.toDouble() / 100.0)
                "${currency.symbol}$formattedNumber"
            }
        }
    }

    companion object {
        fun zero(currency: CurrencyCode = CurrencyCode.IDR): Money = Money(0L, currency)

        fun idr(amount: Long): Money = Money(amount, CurrencyCode.IDR)

        fun idr(amount: Int): Money = Money(amount.toLong(), CurrencyCode.IDR)
    }
}

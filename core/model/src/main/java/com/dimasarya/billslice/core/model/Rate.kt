package com.dimasarya.billslice.core.model

import java.math.BigDecimal
import java.math.RoundingMode

data class Rate(
    val basisPoints: Long,
) {
    val isZero: Boolean
        get() = basisPoints == 0L

    val isPositive: Boolean
        get() = basisPoints > 0L

    val isNegative: Boolean
        get() = basisPoints < 0L

    val percentageDouble: Double
        get() = basisPoints.toDouble() / 100.0

    val percentageBigDecimal: BigDecimal
        get() = BigDecimal(basisPoints).divide(BigDecimal(100), 4, RoundingMode.HALF_UP)

    val multiplierDouble: Double
        get() = basisPoints.toDouble() / 10000.0

    val multiplierBigDecimal: BigDecimal
        get() = BigDecimal(basisPoints).divide(BigDecimal(10000), 8, RoundingMode.HALF_UP)

    fun format(): String {
        return if (basisPoints % 100L == 0L) {
            "${basisPoints / 100L}%"
        } else {
            val formatted = String.format(java.util.Locale.US, "%.2f", percentageDouble)
                .trimEnd('0')
                .trimEnd('.')
            "$formatted%"
        }
    }

    companion object {
        val ZERO = Rate(0L)

        fun fromBasisPoints(basisPoints: Long): Rate {
            return Rate(basisPoints)
        }

        fun fromPercentage(percentage: Int): Rate {
            return Rate(percentage.toLong() * 100L)
        }

        fun fromPercentage(percentage: Long): Rate {
            return Rate(percentage * 100L)
        }

        fun fromPercentage(percentage: Double): Rate {
            val bp = BigDecimal.valueOf(percentage)
                .multiply(BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toLong()
            return Rate(bp)
        }

        fun fromPercentage(percentage: BigDecimal): Rate {
            val bp = percentage.multiply(BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toLong()
            return Rate(bp)
        }
    }
}

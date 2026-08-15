package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.ReceiptTotalStatus
import com.dimasarya.billslice.core.model.ReceiptTotalsValidationResult

class ValidateReceiptTotalsUseCase {

    operator fun invoke(
        calculatedTotal: Money,
        receiptTotal: Money?,
    ): ReceiptTotalsValidationResult {
        if (receiptTotal == null) {
            return ReceiptTotalsValidationResult(
                status = ReceiptTotalStatus.MISSING_TOTAL,
                calculatedTotal = calculatedTotal,
                receiptTotal = null,
                difference = Money.zero(calculatedTotal.currency),
            )
        }

        val difference = receiptTotal - calculatedTotal
        val status = if (difference.isZero) {
            ReceiptTotalStatus.LOOKS_GOOD
        } else {
            ReceiptTotalStatus.NEEDS_REVIEW
        }

        return ReceiptTotalsValidationResult(
            status = status,
            calculatedTotal = calculatedTotal,
            receiptTotal = receiptTotal,
            difference = difference,
        )
    }
}

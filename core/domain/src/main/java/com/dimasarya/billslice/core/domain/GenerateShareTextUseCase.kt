package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillCalculationResult

class GenerateShareTextUseCase {

    operator fun invoke(
        result: BillCalculationResult,
        merchantName: String? = null,
    ): String {
        val builder = StringBuilder()
        val trimmedMerchant = merchantName?.trim().orEmpty()

        if (trimmedMerchant.isNotEmpty()) {
            builder.appendLine("BillSlice result - $trimmedMerchant")
        } else {
            builder.appendLine("BillSlice result")
        }
        builder.appendLine()
        builder.appendLine("Paid by: ${result.payer.name}")
        builder.appendLine()

        val nonPayers = result.participantSplits.filter { !it.isPayer }
        val payerSplit = result.participantSplits.firstOrNull { it.isPayer }

        for (split in nonPayers) {
            builder.appendLine("${split.participant.name} owes ${result.payer.name}: ${split.owesPayer.format()}")
        }

        if (payerSplit != null) {
            builder.appendLine("${payerSplit.participant.name}'s share: ${payerSplit.finalTotal.format()}")
        }

        builder.appendLine()
        builder.appendLine("Total: ${result.total.format()}")
        builder.append("Tax/service included.")

        return builder.toString()
    }
}

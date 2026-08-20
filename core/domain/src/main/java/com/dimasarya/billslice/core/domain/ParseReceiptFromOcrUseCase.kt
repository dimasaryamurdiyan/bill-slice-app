package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.SmartScanParseOutcome

class ParseReceiptFromOcrUseCase(
    private val smartScanRepository: SmartScanRepository,
) {
    suspend operator fun invoke(request: ReceiptParseRequest): SmartScanParseOutcome {
        return smartScanRepository.parseReceipt(request)
    }
}

package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.SmartScanParseOutcome

interface SmartScanRepository {
    suspend fun parseReceipt(request: ReceiptParseRequest): SmartScanParseOutcome
}

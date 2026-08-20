package com.dimasarya.billslice.core.ocr

import android.net.Uri
import com.dimasarya.billslice.core.model.ReceiptOcrOutcome

interface ReceiptOcr {
    suspend fun recognize(imageUri: Uri): ReceiptOcrOutcome
}

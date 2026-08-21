package com.dimasarya.billslice.core.ocr

import android.content.Context
import android.net.Uri
import com.dimasarya.billslice.core.model.ReceiptOcrOutcome
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MlKitReceiptOcr(
    private val context: Context,
) : ReceiptOcr {

    override suspend fun recognize(imageUri: Uri): ReceiptOcrOutcome {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val text = recognizer.process(image).await().text.trim()
            if (text.isEmpty()) ReceiptOcrOutcome.RecognitionFailed else ReceiptOcrOutcome.Success(text)
        } catch (_: CancellationException) {
            ReceiptOcrOutcome.Cancelled
        } catch (exception: MlKitException) {
            if (exception.errorCode == MlKitException.UNAVAILABLE) {
                ReceiptOcrOutcome.ModelDownloadFailed
            } else {
                ReceiptOcrOutcome.RecognitionFailed
            }
        } catch (_: Exception) {
            ReceiptOcrOutcome.UnexpectedFailure
        } finally {
            recognizer.close()
        }
    }
}

private suspend fun Task<Text>.await(): Text = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { text ->
        if (continuation.isActive) continuation.resume(text)
    }
    addOnFailureListener { exception ->
        if (continuation.isActive) continuation.resumeWithException(exception)
    }
    addOnCanceledListener {
        continuation.cancel()
    }
}

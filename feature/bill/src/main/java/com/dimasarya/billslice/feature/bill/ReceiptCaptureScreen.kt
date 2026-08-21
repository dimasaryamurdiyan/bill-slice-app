package com.dimasarya.billslice.feature.bill

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.model.ReceiptOcrOutcome
import com.dimasarya.billslice.feature.bill.components.PrimaryActionButton
import com.dimasarya.billslice.feature.bill.components.SecondaryActionButton
import java.io.File
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import java.util.UUID

@Composable
fun ReceiptCaptureScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEnterManually: () -> Unit,
    onImageSelected: suspend (Uri) -> ReceiptOcrOutcome,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var status by remember { mutableStateOf<ReceiptCaptureStatus?>(null) }
    var ocrText by remember { mutableStateOf<String?>(null) }
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        val uri = cameraUri
        if (captured && uri != null) {
            status = ReceiptCaptureStatus.Reading
            coroutineScope.launch {
                try {
                    val outcome = onImageSelected(uri)
                    ocrText = (outcome as? ReceiptOcrOutcome.Success)?.text
                    status = outcome.toStatus()
                } catch (exception: CancellationException) {
                    status = ReceiptCaptureStatus.Cancelled
                    throw exception
                } finally {
                    context.contentResolver.delete(uri, null, null)
                    cameraUri = null
                }
            }
        } else {
            cameraUri?.let { context.contentResolver.delete(it, null, null) }
            cameraUri = null
            status = ReceiptCaptureStatus.CaptureFailed
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val uri = context.createCameraUri()
            if (uri == null) status = ReceiptCaptureStatus.CaptureFailed else {
                cameraUri = uri
                cameraLauncher.launch(uri)
            }
        } else status = ReceiptCaptureStatus.PermissionDenied
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            status = ReceiptCaptureStatus.Reading
            coroutineScope.launch {
                try {
                    val outcome = onImageSelected(uri)
                    ocrText = (outcome as? ReceiptOcrOutcome.Success)?.text
                    status = outcome.toStatus()
                } catch (exception: CancellationException) {
                    status = ReceiptCaptureStatus.Cancelled
                    throw exception
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(BillSliceThemeTokens.spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.large, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.scan_entry_title),
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(stringResource(R.string.scan_entry_body), style = MaterialTheme.typography.bodyLarge)
        PrimaryActionButton(
            text = stringResource(R.string.scan_camera),
            icon = Icons.Rounded.CameraAlt,
            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        )
        SecondaryActionButton(
            text = stringResource(R.string.scan_import),
            icon = Icons.Rounded.Collections,
            onClick = { importLauncher.launch("image/*") },
        )
        status?.let {
            Text(
                text = stringResource(
                    when (it) {
                        ReceiptCaptureStatus.PermissionDenied -> R.string.scan_permission_denied
                        ReceiptCaptureStatus.Reading -> R.string.scan_reading
                        ReceiptCaptureStatus.TextRead -> R.string.scan_text_read
                        ReceiptCaptureStatus.OcrUnavailable -> R.string.scan_ocr_unavailable
                        ReceiptCaptureStatus.CaptureFailed -> R.string.scan_capture_failed
                        ReceiptCaptureStatus.ModelDownloading -> R.string.scan_model_downloading
                        ReceiptCaptureStatus.RecognitionFailed -> R.string.scan_recognition_failed
                        ReceiptCaptureStatus.Cancelled -> R.string.scan_cancelled
                    },
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Icon(Icons.Rounded.PrivacyTip, contentDescription = null)
        Text(stringResource(R.string.scan_privacy), style = MaterialTheme.typography.bodyMedium)
        SecondaryActionButton(
            text = stringResource(R.string.scan_manual),
            onClick = {
                cameraUri?.let { context.contentResolver.delete(it, null, null) }
                onEnterManually()
            },
        )
        SecondaryActionButton(text = stringResource(R.string.back_to_home), onClick = {
            cameraUri?.let { context.contentResolver.delete(it, null, null) }
            onBack()
        })
    }
}

private enum class ReceiptCaptureStatus {
    PermissionDenied,
    Reading,
    TextRead,
    OcrUnavailable,
    CaptureFailed,
    ModelDownloading,
    RecognitionFailed,
    Cancelled,
}

private fun ReceiptOcrOutcome.toStatus(): ReceiptCaptureStatus = when (this) {
    is ReceiptOcrOutcome.Success -> ReceiptCaptureStatus.TextRead
    ReceiptOcrOutcome.Cancelled -> ReceiptCaptureStatus.Cancelled
    ReceiptOcrOutcome.ModelDownloadFailed -> ReceiptCaptureStatus.ModelDownloading
    ReceiptOcrOutcome.RecognitionFailed -> ReceiptCaptureStatus.RecognitionFailed
    ReceiptOcrOutcome.UnexpectedFailure -> ReceiptCaptureStatus.OcrUnavailable
}

private fun Context.createCameraUri(): Uri? = runCatching {
    val directory = File(this@createCameraUri.cacheDir, "receipt_capture").apply { mkdirs() }
    val file = File(directory, "${UUID.randomUUID()}.jpg")
    FileProvider.getUriForFile(this@createCameraUri, "${this@createCameraUri.packageName}.receipt", file)
}.getOrNull()

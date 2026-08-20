package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ParsedReceiptDraft
import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.SmartScanParseFailure
import com.dimasarya.billslice.core.model.SmartScanParseOutcome
import com.dimasarya.billslice.core.model.SmartScanQuota
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CancellationException

class SmartScanOutcomesTest {

    private val resetAt = 1_788_220_800_000L

    @Test
    fun `quota with remaining scans is available`() {
        val quota = SmartScanQuota(remainingScans = 3, resetsAtEpochMillis = resetAt)

        assertTrue(CanUseSmartScanUseCase()(quota).isAvailable)
    }

    @Test
    fun `quota with no remaining scans is exhausted and retains reset time`() {
        val quota = SmartScanQuota(remainingScans = 0, resetsAtEpochMillis = resetAt)
        val availability = CanUseSmartScanUseCase()(quota)

        assertFalse(availability.isAvailable)
        assertEquals(resetAt, availability.quota.resetsAtEpochMillis)
    }

    @Test
    fun `quota rejects an invalid remaining count or reset time`() {
        assertInvalidQuota(remainingScans = -1, resetsAtEpochMillis = resetAt)
        assertInvalidQuota(remainingScans = 1, resetsAtEpochMillis = 0)
    }

    @Test
    fun `parse request contains only the documented text fields`() {
        val request = ReceiptParseRequest(
            requestId = requestId,
            installId = installId,
            locale = "id-ID",
            currency = CurrencyCode.IDR,
            timezone = "Asia/Jakarta",
            ocrText = "sanitized OCR text",
        )

        assertEquals(installId, request.installId)
        assertEquals(requestId, request.requestId)
        assertEquals("id-ID", request.locale)
        assertEquals(CurrencyCode.IDR, request.currency)
        assertEquals("Asia/Jakarta", request.timezone)
        assertEquals("sanitized OCR text", request.ocrText)
    }

    @Test
    fun `parse request rejects invalid contract metadata`() {
        assertInvalidRequest(requestId = "not-a-uuid", installId = installId, locale = "id-ID", timezone = "Asia/Jakarta")
        assertInvalidRequest(requestId = requestId, installId = "not-a-uuid", locale = "id-ID", timezone = "Asia/Jakarta")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "-", timezone = "Asia/Jakarta")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "id--ID", timezone = "Asia/Jakarta")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "en-a", timezone = "Asia/Jakarta")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "id-ID", timezone = "Not/A_Zone")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "id-ID", timezone = "PST")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "id-ID", timezone = "US/Pacific")
        assertInvalidRequest(requestId = requestId, installId = installId, locale = "id-ID", timezone = "SystemV/PST8PDT")
    }

    @Test
    fun `parse use case preserves coroutine cancellation`() {
        val repository = object : SmartScanRepository {
            override suspend fun parseReceipt(request: ReceiptParseRequest): SmartScanParseOutcome {
                throw CancellationException()
            }
        }

        try {
            runBlocking { ParseReceiptFromOcrUseCase(repository)(request()) }
            fail("Expected cancellation to propagate")
        } catch (_: CancellationException) {
            // Expected: cancellation must not be mapped to a generic failure.
        }
    }

    @Test
    fun `quota exhaustion is a typed parse failure with reset time`() {
        val quota = SmartScanQuota(remainingScans = 0, resetsAtEpochMillis = resetAt)
        val outcome = SmartScanParseOutcome.Failure(SmartScanParseFailure.QuotaExceeded(quota))

        val failure = outcome.failure as SmartScanParseFailure.QuotaExceeded
        assertEquals(quota, failure.quota)
    }

    @Test
    fun `successful parse keeps the receipt draft editable`() {
        val draft = ParsedReceiptDraft(
            id = "draft-id",
            merchantName = null,
            purchasedAt = null,
            currency = CurrencyCode.IDR,
            items = emptyList(),
            serviceRateBasisPoints = null,
            serviceAmountMinor = null,
            taxRateBasisPoints = null,
            taxAmountMinor = null,
            discountMinor = null,
            receiptTotalMinor = null,
        )
        val outcome = SmartScanParseOutcome.Success(
            draft = draft,
            warnings = emptyList(),
            quota = SmartScanQuota(remainingScans = 4, resetsAtEpochMillis = resetAt),
        )

        assertEquals(draft, outcome.draft)
    }

    private fun request(): ReceiptParseRequest = ReceiptParseRequest(
        requestId = requestId,
        installId = installId,
        locale = "id-ID",
        currency = CurrencyCode.IDR,
        timezone = "Asia/Jakarta",
        ocrText = "sanitized OCR text",
    )

    private fun assertInvalidQuota(remainingScans: Int, resetsAtEpochMillis: Long) {
        try {
            SmartScanQuota(remainingScans = remainingScans, resetsAtEpochMillis = resetsAtEpochMillis)
            fail("Expected invalid quota to be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected: invalid server data must not become a valid product quota.
        }
    }

    private fun assertInvalidRequest(requestId: String, installId: String, locale: String, timezone: String) {
        try {
            ReceiptParseRequest(requestId, installId, locale, CurrencyCode.IDR, timezone, "sanitized")
            fail("Expected invalid request metadata to be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }

    private companion object {
        const val requestId = "7dbf4c63-49c0-46c4-9db2-85dd30f583dd"
        const val installId = "83b8dbf6-46c1-4440-9b5f-0aa914773462"
    }
}

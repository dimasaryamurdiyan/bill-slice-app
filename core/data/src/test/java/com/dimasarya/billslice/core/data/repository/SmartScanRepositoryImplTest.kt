package com.dimasarya.billslice.core.data.repository

import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.SmartScanParseFailure
import com.dimasarya.billslice.core.model.SmartScanParseOutcome
import com.dimasarya.billslice.core.network.SmartScanApi
import com.dimasarya.billslice.core.network.SmartScanParseRequestDto
import com.dimasarya.billslice.core.network.SmartScanParseResponseDto
import com.dimasarya.billslice.core.network.SmartScanProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.ConnectException
import java.util.concurrent.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class SmartScanRepositoryImplTest {

    @Test
    fun `repository maps transport failures without exposing exceptions`() {
        assertFailure(UnknownHostException(), SmartScanParseFailure.Offline)
        assertFailure(ConnectException(), SmartScanParseFailure.Offline)
        assertFailure(SocketTimeoutException(), SmartScanParseFailure.Timeout)
        assertFailure(SmartScanProtocolException(), SmartScanParseFailure.MalformedResponse)
        assertFailure(IllegalStateException(), SmartScanParseFailure.UnexpectedFailure)
    }

    @Test
    fun `repository preserves coroutine cancellation`() {
        val repository = repositoryThrowing(CancellationException())

        try {
            runBlocking { repository.parseReceipt(request()) }
            fail("Expected cancellation to propagate")
        } catch (_: CancellationException) {
            // Expected.
        }
    }

    private fun assertFailure(exception: Exception, expected: SmartScanParseFailure) {
        val outcome = runBlocking { repositoryThrowing(exception).parseReceipt(request()) }
        assertEquals(expected, (outcome as SmartScanParseOutcome.Failure).failure)
    }

    private fun repositoryThrowing(exception: Exception) = SmartScanRepositoryImpl(object : SmartScanApi {
        override suspend fun parse(request: SmartScanParseRequestDto): SmartScanParseResponseDto = throw exception
    })

    private fun request() = ReceiptParseRequest(
        requestId = requestId,
        installId = installId,
        locale = "id-ID",
        currency = CurrencyCode.IDR,
        timezone = "Asia/Jakarta",
        ocrText = "sanitized OCR text",
    )

    private companion object {
        const val requestId = "7dbf4c63-49c0-46c4-9db2-85dd30f583dd"
        const val installId = "83b8dbf6-46c1-4440-9b5f-0aa914773462"
    }
}

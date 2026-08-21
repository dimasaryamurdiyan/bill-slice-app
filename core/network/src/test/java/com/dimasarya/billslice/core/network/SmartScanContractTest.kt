package com.dimasarya.billslice.core.network

import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.SmartScanParseFailure
import com.dimasarya.billslice.core.model.SmartScanParseOutcome
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonObject
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartScanContractTest {

    @Test
    fun `request serializes only approved contract fields`() {
        val json = SmartScanJson.codec.encodeToString(SmartScanParseRequestDto.serializer(), request().toDto())
        val fields = SmartScanJson.codec.parseToJsonElement(json).jsonObject.keys

        assertEquals(setOf("requestId", "installId", "locale", "currency", "timezone", "ocrText"), fields)
        assertFalse(json.contains("image"))
    }

    @Test
    fun `production endpoint requires exact HTTPS function URL`() {
        SmartScanEndpoint("https://project.supabase.co/functions/v1/smart-scan-parse", "key")

        listOf(
            "http://project.supabase.co/functions/v1/smart-scan-parse",
            "https://example.com/functions/v1/smart-scan-parse",
            "https://project.supabase.co/functions/v1/other",
            "https://project.supabase.co/functions/v1/smart-scan-parse?secret=value",
        ).forEach { url ->
            try {
                SmartScanEndpoint(url, "key")
                org.junit.Assert.fail("Expected endpoint rejection: $url")
            } catch (_: IllegalArgumentException) {
                // Expected.
            }
        }
    }

    @Test
    fun `retrofit sends exact request and preserves nullable draft values`() = withServer { server ->
        server.enqueue(MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(successBody()))

        val outcome = runBlocking { api(server).parse(request().toDto()) }.toOutcome(request())
        val recorded = server.takeRequest()

        assertEquals("POST", recorded.method)
        assertEquals("/functions/v1/smart-scan-parse", recorded.path)
        assertEquals("publishable-test-key", recorded.getHeader("apikey"))
        assertTrue(recorded.getHeader("Content-Type").orEmpty().startsWith("application/json"))
        assertEquals(
            setOf("requestId", "installId", "locale", "currency", "timezone", "ocrText"),
            SmartScanJson.codec.parseToJsonElement(recorded.body.readUtf8()).jsonObject.keys,
        )

        val success = outcome as SmartScanParseOutcome.Success
        assertNull(success.draft.merchantName)
        assertNull(success.draft.serviceRateBasisPoints)
        assertEquals(10_000L, success.draft.items.single().lineTotalMinor)
        assertEquals("/draft/receiptTotalMinor", success.warnings.first { it.code == "TOTAL_MISSING" }.field)
    }

    @Test
    fun `http quota response maps to typed failure and quota snapshot`() = withServer { server ->
        assertEquals(1, Regex("\"error\"\\s*:").findAll(quotaBody()).count())
        server.enqueue(MockResponse().setResponseCode(429).setHeader("Content-Type", "application/json").setBody(quotaBody()))

        val outcome = runBlocking { api(server).parse(request().toDto()) }.toOutcome(request())

        assertTrue(outcome is SmartScanParseOutcome.Failure)
        val failure = (outcome as SmartScanParseOutcome.Failure).failure as SmartScanParseFailure.QuotaExceeded
        assertEquals(0, failure.quota.remainingScans)
        assertEquals(5, failure.quota.limit)
    }

    @Test
    fun `response for another request is rejected`() {
        val response = SmartScanJson.codec.decodeFromString(SmartScanParseResponseDto.serializer(), successBody("other-request"))

        val outcome = response.toOutcome(request())

        assertEquals(SmartScanParseFailure.MalformedResponse, (outcome as SmartScanParseOutcome.Failure).failure)
    }

    @Test
    fun `contract errors keep actionable semantics`() {
        assertEquals(SmartScanParseFailure.PolicyUnavailable, errorOutcome("POLICY_UNAVAILABLE", true))
        assertEquals(SmartScanParseFailure.ReplayExpired, errorOutcome("REPLAY_EXPIRED", false))
        assertEquals(SmartScanParseFailure.ConfigurationUnavailable, errorOutcome("INVALID_API_KEY", false))
        assertEquals(SmartScanParseFailure.Timeout, errorOutcome("PARSER_TIMEOUT", true))
        assertEquals(
            SmartScanParseFailure.ServerRejected("RATE_LIMITED", true, 10),
            errorOutcome("RATE_LIMITED", true, 10),
        )
        val invalidRequest = SmartScanParseResponseDto(
            requestId = null,
            status = "error",
            quota = null,
            error = SmartScanErrorDto("INVALID_REQUEST", false, null),
        ).toOutcome(request()) as SmartScanParseOutcome.Failure
        assertEquals(SmartScanParseFailure.ServerRejected("INVALID_REQUEST", false, null), invalidRequest.failure)
    }

    @Test
    fun `http status that disagrees with envelope is malformed`() = withServer { server ->
        server.enqueue(MockResponse().setResponseCode(500).setHeader("Content-Type", "application/json").setBody(quotaBody()))

        try {
            runBlocking { api(server).parse(request().toDto()) }
            org.junit.Assert.fail("Expected protocol failure")
        } catch (_: SmartScanProtocolException) {
            // The HTTP status and sanitized envelope must agree.
        }
    }

    @Test
    fun `malformed JSON becomes protocol failure`() = withServer { server ->
        server.enqueue(MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody("{"))

        try {
            runBlocking { api(server).parse(request().toDto()) }
            org.junit.Assert.fail("Expected protocol failure")
        } catch (_: SmartScanProtocolException) {
            // Expected.
        }
    }

    @Test
    fun `omitted required nullable member becomes protocol failure`() = withServer { server ->
        val missingServiceAmount = successBody().replace("\"serviceAmountMinor\":null,", "")
        server.enqueue(
            MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(missingServiceAmount),
        )

        try {
            runBlocking { api(server).parse(request().toDto()) }
            org.junit.Assert.fail("Expected protocol failure")
        } catch (_: SmartScanProtocolException) {
            // Expected.
        }
    }

    @Test
    fun `omitted nullable envelope members become protocol failures`() = withServer { server ->
        val validError = errorBody("REQUEST_CONFLICT", retryable = false)
        val bodies = listOf(
            validError.replace("\"requestId\":\"$requestId\",", ""),
            validError.replace(",\"retryAfterSeconds\":null", ""),
            validError.replace(",\"quota\":null", ""),
        )

        bodies.forEach { body ->
            server.enqueue(MockResponse().setResponseCode(409).setHeader("Content-Type", "application/json").setBody(body))
            try {
                runBlocking { api(server).parse(request().toDto()) }
                org.junit.Assert.fail("Expected protocol failure")
            } catch (_: SmartScanProtocolException) {
                // Expected.
            }
        }
    }

    @Test
    fun `contradictory retry metadata becomes protocol failure`() = withServer { server ->
        server.enqueue(
            MockResponse().setResponseCode(409).setHeader("Content-Type", "application/json").setBody(
                errorBody("REQUEST_CONFLICT", retryable = true),
            ),
        )

        try {
            runBlocking { api(server).parse(request().toDto()) }
            org.junit.Assert.fail("Expected protocol failure")
        } catch (_: SmartScanProtocolException) {
            // Expected.
        }
    }

    @Test
    fun `invalid draft field or missing uncertainty warning is malformed`() {
        val invalidAmount = validResponse().copy(draft = validDraft().copy(taxAmountMinor = -1))
        val invalidDate = validResponse().copy(draft = validDraft().copy(purchasedAt = "not-a-date"))
        val missingSeconds = validResponse().copy(draft = validDraft().copy(purchasedAt = "2026-08-20T19:30+07:00"))
        val offsetSeconds = validResponse().copy(draft = validDraft().copy(purchasedAt = "2026-08-20T19:30:00+07:00:30"))
        val missingWarning = validResponse().copy(warnings = validWarnings().filterNot { it.code == "MERCHANT_UNCERTAIN" })
        val invalidPointer = validResponse().copy(
            warnings = validWarnings().map { if (it.code == "TOTAL_MISSING") it.copy(field = "/draft/~2total") else it },
        )

        assertMalformed(invalidAmount)
        assertMalformed(invalidDate)
        assertMalformed(missingSeconds)
        assertMalformed(offsetSeconds)
        assertMalformed(missingWarning)
        assertMalformed(invalidPointer)
    }

    @Test
    fun `quota policy rules accept fixed fair use without monthly reset`() {
        val quota = SmartScanQuotaDto("pro", "fair_use", used = 2, limit = 10, remaining = 8, resetsAt = null)
        val outcome = validResponse().copy(quota = quota).toOutcome(request()) as SmartScanParseOutcome.Success

        assertEquals(8, outcome.quota.remainingScans)
        assertNull(outcome.quota.resetsAtEpochMillis)

        assertMalformed(validResponse().copy(quota = SmartScanQuotaDto("free", "monthly_5", 1, null, null, null)))
    }

    private fun api(server: MockWebServer) = RetrofitSmartScanApi.createForTesting(
        endpointUrl = server.url("/functions/v1/smart-scan-parse").toString(),
        publishableKey = "publishable-test-key",
    )

    private fun request() = ReceiptParseRequest(
        requestId = requestId,
        installId = installId,
        locale = "id-ID",
        currency = CurrencyCode.IDR,
        timezone = "Asia/Jakarta",
        ocrText = "sanitized OCR text",
    )

    private fun successBody(requestId: String = Companion.requestId) = """
        {
          "requestId":"$requestId","status":"success",
          "draft":{"merchantName":null,"purchasedAt":null,"currency":"IDR","items":[{"name":"Nasi","quantity":1,"unitPriceMinor":10000,"lineTotalMinor":10000}],"serviceRateBasisPoints":null,"serviceAmountMinor":null,"taxRateBasisPoints":1000,"taxAmountMinor":1000,"discountMinor":null,"receiptTotalMinor":null},
          "warnings":[{"code":"MERCHANT_UNCERTAIN","field":"/draft/merchantName"},{"code":"PURCHASED_AT_UNCERTAIN","field":"/draft/purchasedAt"},{"code":"SERVICE_UNCERTAIN","field":null},{"code":"DISCOUNT_UNCERTAIN","field":"/draft/discountMinor"},{"code":"TOTAL_MISSING","field":"/draft/receiptTotalMinor"}],
          "quota":{"plan":"free","policy":"monthly_5","used":1,"limit":5,"remaining":4,"resetsAt":"2026-09-01T00:00:00+07:00"}
        }
    """.trimIndent()

    private fun quotaBody() = """
        {
          "requestId":"$requestId","status":"error",
          "error":{"code":"QUOTA_EXHAUSTED","retryable":false,"retryAfterSeconds":null},
          "quota":{"plan":"free","policy":"monthly_5","used":5,"limit":5,"remaining":0,"resetsAt":"2026-09-01T00:00:00+07:00"}
        }
    """.trimIndent()

    private fun errorBody(code: String, retryable: Boolean) = """
        {"requestId":"$requestId","status":"error","error":{"code":"$code","retryable":$retryable,"retryAfterSeconds":null},"quota":null}
    """.trimIndent()

    private fun validResponse() = SmartScanParseResponseDto(
        requestId = requestId,
        status = "success",
        draft = validDraft(),
        warnings = validWarnings(),
        quota = SmartScanQuotaDto("free", "monthly_5", 1, 5, 4, "2026-09-01T00:00:00+07:00"),
    )

    private fun validDraft() = SmartScanDraftDto(
        merchantName = null,
        purchasedAt = null,
        currency = "IDR",
        items = listOf(SmartScanItemDto("Nasi", 1, 10_000, 10_000)),
        serviceRateBasisPoints = null,
        serviceAmountMinor = null,
        taxRateBasisPoints = 1_000,
        taxAmountMinor = 1_000,
        discountMinor = null,
        receiptTotalMinor = null,
    )

    private fun validWarnings() = listOf(
        SmartScanWarningDto("MERCHANT_UNCERTAIN", "/draft/merchantName"),
        SmartScanWarningDto("PURCHASED_AT_UNCERTAIN", "/draft/purchasedAt"),
        SmartScanWarningDto("SERVICE_UNCERTAIN", null),
        SmartScanWarningDto("DISCOUNT_UNCERTAIN", "/draft/discountMinor"),
        SmartScanWarningDto("TOTAL_MISSING", "/draft/receiptTotalMinor"),
    )

    private fun assertMalformed(response: SmartScanParseResponseDto) {
        val outcome = response.toOutcome(request()) as SmartScanParseOutcome.Failure
        assertEquals(SmartScanParseFailure.MalformedResponse, outcome.failure)
    }

    private fun errorOutcome(
        code: String,
        retryable: Boolean,
        retryAfterSeconds: Int? = null,
    ): SmartScanParseFailure {
        val outcome = SmartScanParseResponseDto(
            requestId = requestId,
            status = "error",
            quota = null,
            error = SmartScanErrorDto(code, retryable, retryAfterSeconds),
        ).toOutcome(request())
        return (outcome as SmartScanParseOutcome.Failure).failure
    }

    private fun withServer(block: (MockWebServer) -> Unit) {
        val server = MockWebServer()
        server.start()
        try {
            block(server)
        } finally {
            server.shutdown()
        }
    }

    private companion object {
        const val requestId = "7dbf4c63-49c0-46c4-9db2-85dd30f583dd"
        const val installId = "83b8dbf6-46c1-4440-9b5f-0aa914773462"
    }
}

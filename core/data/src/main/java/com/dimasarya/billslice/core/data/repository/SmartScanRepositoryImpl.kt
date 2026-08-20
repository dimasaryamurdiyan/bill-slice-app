package com.dimasarya.billslice.core.data.repository

import com.dimasarya.billslice.core.domain.SmartScanRepository
import com.dimasarya.billslice.core.model.ReceiptParseRequest
import com.dimasarya.billslice.core.model.SmartScanParseFailure
import com.dimasarya.billslice.core.model.SmartScanParseOutcome
import com.dimasarya.billslice.core.network.SmartScanApi
import com.dimasarya.billslice.core.network.SmartScanProtocolException
import com.dimasarya.billslice.core.network.toDto
import com.dimasarya.billslice.core.network.toOutcome
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException
import kotlinx.coroutines.CancellationException

class SmartScanRepositoryImpl(
    private val api: SmartScanApi,
) : SmartScanRepository {

    override suspend fun parseReceipt(request: ReceiptParseRequest): SmartScanParseOutcome = try {
        api.parse(request.toDto()).toOutcome(request)
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: UnknownHostException) {
        SmartScanParseOutcome.Failure(SmartScanParseFailure.Offline)
    } catch (_: SocketTimeoutException) {
        SmartScanParseOutcome.Failure(SmartScanParseFailure.Timeout)
    } catch (_: SmartScanProtocolException) {
        SmartScanParseOutcome.Failure(SmartScanParseFailure.MalformedResponse)
    } catch (_: IOException) {
        SmartScanParseOutcome.Failure(SmartScanParseFailure.Offline)
    } catch (_: Exception) {
        SmartScanParseOutcome.Failure(SmartScanParseFailure.UnexpectedFailure)
    }
}

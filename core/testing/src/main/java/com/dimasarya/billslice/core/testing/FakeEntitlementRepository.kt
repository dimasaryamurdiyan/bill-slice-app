package com.dimasarya.billslice.core.testing

import com.dimasarya.billslice.core.domain.EntitlementRepository
import com.dimasarya.billslice.core.domain.EntitlementStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeEntitlementRepository(
    initialStatus: EntitlementStatus = EntitlementStatus.FREE,
) : EntitlementRepository {
    private val _status = MutableStateFlow(initialStatus)

    fun setStatus(status: EntitlementStatus) {
        _status.value = status
    }

    override fun observeEntitlement(): Flow<EntitlementStatus> = _status.asStateFlow()
}

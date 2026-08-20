package com.dimasarya.billslice.core.domain

import kotlinx.coroutines.flow.Flow

enum class EntitlementStatus {
    FREE,
    PRO,
}

interface EntitlementRepository {
    fun observeEntitlement(): Flow<EntitlementStatus>
}

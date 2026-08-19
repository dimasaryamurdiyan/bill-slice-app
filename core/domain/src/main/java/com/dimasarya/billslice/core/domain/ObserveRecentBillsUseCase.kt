package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.RecentBillSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class ObserveRecentBillsUseCase(
    private val billRepository: BillRepository,
    private val entitlementRepository: EntitlementRepository = DefaultFakeEntitlementRepository,
) {
    operator fun invoke(): Flow<List<RecentBillSummary>> {
        return combine(
            billRepository.observeBills(),
            entitlementRepository.observeEntitlement(),
        ) { bills, entitlement ->
            when (entitlement) {
                EntitlementStatus.PRO -> bills
                EntitlementStatus.FREE -> bills.take(FREE_HISTORY_LIMIT)
            }
        }
    }

    companion object {
        const val FREE_HISTORY_LIMIT = 5
    }
}

private object DefaultFakeEntitlementRepository : EntitlementRepository {
    override fun observeEntitlement(): Flow<EntitlementStatus> = flowOf(EntitlementStatus.FREE)
}

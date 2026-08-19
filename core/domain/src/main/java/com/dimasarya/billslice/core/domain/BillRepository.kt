package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.RecentBillSummary
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    suspend fun saveBill(draft: BillDraft, calculationResult: BillCalculationResult): Result<Unit>
    fun observeBills(): Flow<List<RecentBillSummary>>
    suspend fun getBill(id: String): Result<BillDraft>
}

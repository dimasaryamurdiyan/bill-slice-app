package com.dimasarya.billslice.core.testing

import com.dimasarya.billslice.core.domain.BillRepository
import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.RecentBillSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBillRepository(
    initialBills: List<Pair<BillDraft, BillCalculationResult>> = emptyList(),
) : BillRepository {

    private val billsMap = MutableStateFlow(initialBills.associateBy { it.first.id })
    var shouldFailSave: Boolean = false
    var shouldFailGet: Boolean = false

    override suspend fun saveBill(draft: BillDraft, calculationResult: BillCalculationResult): Result<Unit> {
        if (shouldFailSave) {
            return Result.failure(IllegalStateException("Simulated disk error"))
        }
        billsMap.value = billsMap.value + (draft.id to (draft to calculationResult))
        return Result.success(Unit)
    }

    override fun observeBills(): Flow<List<RecentBillSummary>> {
        return billsMap.map { map ->
            map.values.map { (draft, result) ->
                RecentBillSummary(
                    id = draft.id,
                    merchantName = draft.merchantName.ifBlank { "Untitled Bill" },
                    createdAtEpochMillis = draft.createdAtEpochMillis,
                    total = result.total,
                    participantCount = draft.participants.size,
                    currency = draft.currency,
                )
            }.sortedByDescending { it.createdAtEpochMillis }
        }
    }

    override suspend fun getBill(id: String): Result<BillDraft> {
        if (shouldFailGet) {
            return Result.failure(IllegalStateException("Simulated read error"))
        }
        val draft = billsMap.value[id]?.first
            ?: return Result.failure(NoSuchElementException("Bill $id not found"))
        return Result.success(draft)
    }
}

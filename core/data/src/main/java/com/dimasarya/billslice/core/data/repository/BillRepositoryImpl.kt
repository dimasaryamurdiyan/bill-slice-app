package com.dimasarya.billslice.core.data.repository

import com.dimasarya.billslice.core.data.mapper.BillEntityMapper
import com.dimasarya.billslice.core.database.dao.BillDao
import com.dimasarya.billslice.core.domain.BillRepository
import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.core.model.RecentBillSummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BillRepositoryImpl(
    private val billDao: BillDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BillRepository {

    override suspend fun saveBill(
        draft: BillDraft,
        calculationResult: BillCalculationResult,
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            val (billEntity, details) = BillEntityMapper.toEntities(draft, calculationResult)
            val (items, participants, assignments) = details
            billDao.upsertCompleteBill(
                bill = billEntity,
                items = items,
                participants = participants,
                assignments = assignments,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeBills(): Flow<List<RecentBillSummary>> {
        return billDao.observeBillsWithDetails()
            .map { list ->
                list.mapNotNull { BillEntityMapper.toSummaryOrNull(it) }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getBill(id: String): Result<BillDraft> = withContext(ioDispatcher) {
        try {
            val details = billDao.getBillWithDetails(id)
                ?: return@withContext Result.failure(NoSuchElementException("Bill $id not found"))
            Result.success(BillEntityMapper.toDomainDraft(details))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

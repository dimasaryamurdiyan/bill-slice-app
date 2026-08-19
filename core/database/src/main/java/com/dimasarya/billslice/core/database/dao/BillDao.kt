package com.dimasarya.billslice.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dimasarya.billslice.core.database.model.BillEntity
import com.dimasarya.billslice.core.database.model.BillItemEntity
import com.dimasarya.billslice.core.database.model.BillWithDetails
import com.dimasarya.billslice.core.database.model.ItemAssignmentEntity
import com.dimasarya.billslice.core.database.model.ParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Transaction
    @Query("SELECT * FROM bills ORDER BY createdAtEpochMillis DESC")
    fun observeBillsWithDetails(): Flow<List<BillWithDetails>>

    @Transaction
    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getBillWithDetails(id: String): BillWithDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBill(bill: BillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<BillItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<ItemAssignmentEntity>)

    @Query("DELETE FROM bill_items WHERE billId = :billId")
    suspend fun deleteItemsForBill(billId: String)

    @Query("DELETE FROM participants WHERE billId = :billId")
    suspend fun deleteParticipantsForBill(billId: String)

    @Query("DELETE FROM item_assignments WHERE billId = :billId")
    suspend fun deleteAssignmentsForBill(billId: String)

    @Transaction
    suspend fun upsertCompleteBill(
        bill: BillEntity,
        items: List<BillItemEntity>,
        participants: List<ParticipantEntity>,
        assignments: List<ItemAssignmentEntity>,
    ) {
        insertOrUpdateBill(bill)
        deleteItemsForBill(bill.id)
        deleteParticipantsForBill(bill.id)
        deleteAssignmentsForBill(bill.id)
        if (items.isNotEmpty()) insertItems(items)
        if (participants.isNotEmpty()) insertParticipants(participants)
        if (assignments.isNotEmpty()) insertAssignments(assignments)
    }
}

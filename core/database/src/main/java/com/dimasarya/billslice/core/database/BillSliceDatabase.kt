package com.dimasarya.billslice.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dimasarya.billslice.core.database.dao.BillDao
import com.dimasarya.billslice.core.database.model.BillEntity
import com.dimasarya.billslice.core.database.model.BillItemEntity
import com.dimasarya.billslice.core.database.model.ItemAssignmentEntity
import com.dimasarya.billslice.core.database.model.ParticipantEntity

@Database(
    entities = [
        BillEntity::class,
        BillItemEntity::class,
        ParticipantEntity::class,
        ItemAssignmentEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class BillSliceDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao

    companion object {
        const val DATABASE_NAME = "billslice_history.db"

        fun buildInMemory(context: Context): BillSliceDatabase {
            return Room.inMemoryDatabaseBuilder(context, BillSliceDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }

        fun buildDatabase(context: Context): BillSliceDatabase {
            return Room.databaseBuilder(context, BillSliceDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}

object BillSliceDatabaseProvider {
    fun createBillDao(context: Context): BillDao {
        return BillSliceDatabase.buildDatabase(context).billDao()
    }
}

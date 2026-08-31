package com.psildave.punchtheclock.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.psildave.punchtheclock.data.local.dao.OfflinePunchDao
import com.psildave.punchtheclock.data.local.entity.OfflinePunchEntity

@Database(entities = [OfflinePunchEntity::class], version = 1, exportSchema = false)
abstract class PunchDatabase : RoomDatabase() {

    abstract fun offlinePunchDao(): OfflinePunchDao

    companion object {
        @Volatile
        private var INSTANCE: PunchDatabase? = null

        fun getDatabase(context: Context): PunchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PunchDatabase::class.java,
                    "punch_offline_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.psildave.punchtheclock.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PunchEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun punchDao(): PunchDao
}
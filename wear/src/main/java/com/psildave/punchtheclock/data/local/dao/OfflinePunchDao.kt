package com.psildave.punchtheclock.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psildave.punchtheclock.data.local.entity.OfflinePunchEntity

@Dao
interface OfflinePunchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPunch(punch: OfflinePunchEntity)

    @Query("SELECT * FROM offline_punches ORDER BY timestamp ASC")
    suspend fun getAllOfflinePunches(): List<OfflinePunchEntity>

    @Query("DELETE FROM offline_punches WHERE id = :id")
    suspend fun deletePunch(id: Long)
}
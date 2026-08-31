package com.psildave.punchtheclock.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the punch records entity.
 *
 * Defines database operations for saving and retrieving time clock punches.
 */
@Dao
interface PunchDao {
    /**
     * Inserts a new punch record or replaces an existing one if there is a conflict.
     *
     * @param punch The punch record entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPunch(punch: PunchEntity)

    /**
     * Retrieves all punch records ordered by timestamp in descending order.
     *
     * @return A [Flow] containing a list of [PunchEntity].
     */
    @Query("SELECT * FROM punch_records ORDER BY timestamp DESC")
    fun getAllPunches(): Flow<List<PunchEntity>>

    /**
     * Checks if a punch of the same type exists within a specific time window.
     * Used for de-duplication.
     */
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM punch_records 
            WHERE punchType = :type 
            AND timestamp BETWEEN :startTime AND :endTime
        )
    """)
    suspend fun existsPunchInRange(type: String, startTime: Long, endTime: Long): Boolean
}

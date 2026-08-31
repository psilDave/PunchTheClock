package com.psildave.punchtheclock.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_punches")
data class OfflinePunchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val punchType: String,
    val label: String,
    val timestamp: Long = System.currentTimeMillis()
)
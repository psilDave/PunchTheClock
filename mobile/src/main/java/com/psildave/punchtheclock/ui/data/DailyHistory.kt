package com.psildave.punchtheclock.ui.data

import com.psildave.punchtheclock.data.database.PunchEntity

data class DailyHistory(
    val dateText: String,
    val totalWorkedText: String,
    val punches: List<PunchEntity>
)

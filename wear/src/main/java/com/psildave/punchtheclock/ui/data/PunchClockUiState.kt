package com.psildave.punchtheclock.ui.data

import com.psildave.punchtheclock.shared.model.PunchType

/**
 * State representing the content of the Home screen.
 */
data class PunchClockUiState(
    val type: PunchType,
    val currentTime: String,
    val nextAction: String
)

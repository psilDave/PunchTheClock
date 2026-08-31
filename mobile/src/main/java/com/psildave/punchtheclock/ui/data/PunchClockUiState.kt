package com.psildave.punchtheclock.ui.data

import androidx.annotation.StringRes

sealed class PunchClockUiState {
    data object Loading : PunchClockUiState()
    data class Success(
        val totalWorkedText: String,
        @StringRes val statusResId: Int,
        val isTodayEmpty: Boolean
    ) : PunchClockUiState()
}
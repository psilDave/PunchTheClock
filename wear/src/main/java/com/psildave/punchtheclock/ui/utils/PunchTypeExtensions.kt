package com.psildave.punchtheclock.ui.utils

import androidx.annotation.StringRes
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType

@get:StringRes
val PunchType.titleRes: Int
    get() = when (this) {
        PunchType.CLOCK_IN -> R.string.punch_type_clock_in
        PunchType.LUNCH -> R.string.punch_type_lunch
        PunchType.CLOCK_OUT -> R.string.punch_type_clock_out
        PunchType.OTHER -> R.string.punch_type_other
    }
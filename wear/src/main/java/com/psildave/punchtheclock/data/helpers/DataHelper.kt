package com.psildave.punchtheclock.data.helpers

import java.text.DateFormat
import java.util.Date
import java.util.Locale

/**
 * Returns a formatted string representing the current system time.
 *
 * @return Formatted time string according to the system's short date format and locale.
 */
fun getCurrentTimeString(): String {
    val df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
    return df.format(Date())
}
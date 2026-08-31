package com.psildave.punchtheclock.data.helpers

import java.util.Calendar

/**
 * Helper class for alarm-related calculations.
 */
object AlarmHelper {
    /**
     * Calculates the next occurrence of an alarm based on time and days of the week.
     *
     * @param hour The hour of the day (0-23).
     * @param minute The minute of the hour (0-59).
     * @param daysOfWeek List of days (Calendar.SUNDAY to Calendar.SATURDAY).
     * @return A Calendar instance set to the next occurrence.
     */
    fun getNextOccurrence(hour: Int, minute: Int, daysOfWeek: List<Int>): Calendar {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If daysOfWeek is empty, we consider it disabled or effectively everyday 
        // based on implementation. Here we treat empty as everyday for safety,
        // but the calling code should ideally only schedule if isEnabled and !daysOfWeek.isEmpty().
        val enabledDays = if (daysOfWeek.isEmpty()) (1..7).toList() else daysOfWeek

        // If today is an enabled day and the time hasn't passed, use today
        if (enabledDays.contains(target.get(Calendar.DAY_OF_WEEK)) && target.after(now)) {
            return target
        }

        // Otherwise, find the next enabled day (up to 7 days ahead)
        (1..7).forEach { _ ->
            target.add(Calendar.DAY_OF_YEAR, 1)
            if (enabledDays.contains(target.get(Calendar.DAY_OF_WEEK))) {
                return target
            }
        }

        return target
    }
}

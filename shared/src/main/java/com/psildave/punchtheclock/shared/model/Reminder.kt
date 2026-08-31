package com.psildave.punchtheclock.shared.model

import java.util.UUID

/**
 * Data model representing a time clock punch reminder.
 *
 * @property id Unique identifier for the reminder, automatically generated as a UUID if not provided.
 * @property time The scheduled time for the reminder (usually in HH:mm format).
 * @property label Description or label for the reminder (e.g., "Clock In", "Lunch").
 * @property punchType The type of punch associated with this reminder (reference to [PunchType] name).
 * @property isEnabled Indicates whether the reminder is active and should trigger alarms.
 * @property daysOfWeek List of days when the reminder is active (using Calendar.SUNDAY to Calendar.SATURDAY).
 */
data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val time: String,
    val label: String,
    val punchType: String,
    val isEnabled: Boolean,
    val daysOfWeek: List<Int>? = listOf(2, 3, 4, 5, 6)
)

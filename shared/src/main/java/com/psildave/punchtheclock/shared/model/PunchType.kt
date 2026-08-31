package com.psildave.punchtheclock.shared.model

import androidx.compose.ui.graphics.Color

/**
 * Represents the different types of time clock punches available in the system.
 *
 * Each type has an associated color for visual representation in the UI.
 *
 * @property color The color used to represent this punch type.
 */
enum class PunchType(val color: Color) {
    CLOCK_IN(Color(0xFF4DEAA1)),
    LUNCH(Color(0xFFFF9800)),
    CLOCK_OUT(Color(0xFF63A4FF)),
    OTHER(Color(0xFFB388FF));

    companion object {
        /**
         * Converts a name string into a [PunchType].
         *
         * @param name The name of the punch type.
         * @return The corresponding [PunchType] or [OTHER] if not found.
         */
        fun fromName(name: String): PunchType {
            return entries.find { it.name == name } ?: OTHER
        }
    }
}
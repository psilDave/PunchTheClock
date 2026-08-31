package com.psildave.punchtheclock.data.preferences

import java.util.UUID

data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val time: String,
    val label: String,
    val isEnabled: Boolean
)
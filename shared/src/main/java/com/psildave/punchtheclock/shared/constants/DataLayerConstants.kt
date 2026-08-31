package com.psildave.punchtheclock.shared.constants

/**
 * Constants used for communication between the mobile device and the wearable via the Wear OS Data Layer.
 */
object DataLayerConstants {
    const val PUNCH_EVENT_PATH = "/punch_event"
    const val REMINDERS_PATH = "/punch_reminders"
    
    const val KEY_REMINDERS_JSON = "reminders_json"
    const val KEY_TIMESTAMP = "timestamp"
}
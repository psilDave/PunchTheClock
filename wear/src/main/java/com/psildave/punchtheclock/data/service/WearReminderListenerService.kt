package com.psildave.punchtheclock.data.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.psildave.punchtheclock.data.helpers.AlarmHelper
import com.psildave.punchtheclock.data.local.WatchStorage
import com.psildave.punchtheclock.data.receiver.PunchAlarmReceiver
import com.psildave.punchtheclock.shared.constants.DataLayerConstants
import com.psildave.punchtheclock.shared.constants.IntentConstants
import com.psildave.punchtheclock.shared.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service that listens for changes in the Data Layer on the Wear OS device.
 *
 * It receives reminder updates from the mobile app, persists them locally,
 * and manages the system alarms for each reminder.
 */
class WearReminderListenerService : WearableListenerService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()

    /**
     * Triggered when data in the Wearable Data Layer changes.
     *
     * Detects changes in the reminders path and processes the updated list.
     *
     * @param dataEvents A buffer containing data events.
     */
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == DataLayerConstants.REMINDERS_PATH) {

                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val json = dataMap.getString(DataLayerConstants.KEY_REMINDERS_JSON)

                if (json != null) {
                    Log.d(LOG_TAG, "Reminders JSON received: $json")

                    val type = object : TypeToken<List<Reminder>>() {}.type
                    val remindersList: List<Reminder> = gson.fromJson(json, type)

                    serviceScope.launch {
                        val oldReminders = WatchStorage.getReminders(applicationContext)
                        WatchStorage.saveReminders(applicationContext, remindersList)
                        scheduleAlarmsOnWatch(oldReminders, remindersList)
                    }
                }
            }
        }
    }

    /**
     * Schedules or cancels system alarms based on the provided list of reminders.
     *
     * It compares the new list with the old one to identify deleted reminders
     * and ensure their alarms are removed from the system.
     *
     * @param oldReminders The previous list of reminders.
     * @param newReminders The new list of [Reminder] objects to schedule.
     */
    private fun scheduleAlarmsOnWatch(oldReminders: List<Reminder>, newReminders: List<Reminder>) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // 1. Identify deleted reminders and cancel their alarms
        val newIds = newReminders.map { it.id }.toSet()
        val deletedReminders = oldReminders.filter { it.id !in newIds }

        deletedReminders.forEach { reminder ->
            val pendingIntent = createPendingIntent(reminder)
            alarmManager.cancel(pendingIntent)
            Log.d(LOG_TAG, "Alarm [${reminder.label}] deleted. Canceled in the system.")
        }

        // 2. Process new or updated reminders
        newReminders.forEach { reminder ->
            val pendingIntent = createPendingIntent(reminder)

            alarmManager.cancel(pendingIntent)

            if (reminder.isEnabled && reminder.daysOfWeek != null && reminder.daysOfWeek!!.isNotEmpty()) {
                val parts = reminder.time.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                val calendar = AlarmHelper.getNextOccurrence(hour, minute, reminder.daysOfWeek!!)

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d(LOG_TAG, "Alarm [${reminder.label}] scheduled for ${calendar.time}")
                } catch (e: SecurityException) {
                    Log.e(LOG_TAG, "Missing exact alarm permission!", e)
                }
            } else {
                Log.d(LOG_TAG, "Alarm [${reminder.label}] disabled or no days selected. Canceled.")
            }
        }
    }

    /**
     * Creates a PendingIntent for the given reminder.
     *
     * @param reminder The reminder to create the intent for.
     * @return A PendingIntent for the alarm.
     */
    private fun createPendingIntent(reminder: Reminder): PendingIntent {
        val intent = Intent(this, PunchAlarmReceiver::class.java).apply {
            putExtra(IntentConstants.EXTRA_PUNCH_ID, reminder.id)
            putExtra(IntentConstants.EXTRA_PUNCH_LABEL, reminder.label)
            putExtra(IntentConstants.EXTRA_PUNCH_TIME, reminder.time)
            putExtra(IntentConstants.EXTRA_PUNCH_TYPE, reminder.punchType)
            val daysList = reminder.daysOfWeek ?: listOf(2, 3, 4, 5, 6)
            putIntegerArrayListExtra(IntentConstants.EXTRA_DAYS_OF_WEEK, ArrayList(daysList))
        }

        return PendingIntent.getBroadcast(
            this,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val LOG_TAG = "WearReminderService"
    }
}
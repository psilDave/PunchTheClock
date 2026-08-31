package com.psildave.punchtheclock.data.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.psildave.punchtheclock.data.helpers.AlarmHelper
import com.psildave.punchtheclock.data.local.WatchStorage
import com.psildave.punchtheclock.shared.constants.IntentConstants

/**
 * BroadcastReceiver responsible for restoring punch reminders alarms when the device is rebooted.
 *
 * It listens for BOOT_COMPLETED actions and restores all active reminders saved in local storage.
 */
class BootReceiver : BroadcastReceiver() {

    /**
     * Called when the device receives a reboot broadcast.
     *
     * @param context The context in which the receiver is running.
     * @param intent The Intent containing the broadcast action.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == IntentConstants.ACTION_QUICKBOOT_POWERON) {
            Log.d(LOG_TAG, "Watch restarted. Restoring punch alarms...")

            val savedReminders = WatchStorage.getReminders(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            savedReminders.filter { it.isEnabled && it.daysOfWeek != null && it.daysOfWeek!!.isNotEmpty() }
                .forEach { reminder ->
                    val parts = reminder.time.split(":")
                    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                    val calendar =
                        AlarmHelper.getNextOccurrence(hour, minute, reminder.daysOfWeek!!)

                    val alarmIntent = Intent(context, PunchAlarmReceiver::class.java).apply {
                        putExtra(IntentConstants.EXTRA_PUNCH_ID, reminder.id)
                        putExtra(IntentConstants.EXTRA_PUNCH_LABEL, reminder.label)
                        putExtra(IntentConstants.EXTRA_PUNCH_TIME, reminder.time)
                        putExtra(IntentConstants.EXTRA_PUNCH_TYPE, reminder.punchType)
                        putIntegerArrayListExtra(
                            IntentConstants.EXTRA_DAYS_OF_WEEK,
                            ArrayList(reminder.daysOfWeek!!)
                        )
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        reminder.id.hashCode(),
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    try {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d(LOG_TAG, "Alarm restored: ${reminder.label} for ${calendar.time}")
                    } catch (e: SecurityException) {
                        Log.e(LOG_TAG, "Permission error when restoring alarm on boot", e)
                    }
                }
        }
    }

    companion object {
        private const val LOG_TAG = "BootReceiver"
    }
}

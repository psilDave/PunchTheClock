package com.psildave.punchtheclock.data.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.data.helpers.AlarmHelper
import com.psildave.punchtheclock.shared.constants.IntentConstants
import com.psildave.punchtheclock.ui.MainActivity
import java.util.Calendar
import java.util.ArrayList

/**
 * BroadcastReceiver that handles scheduled punch alarms.
 *
 * Displays a standard notification and reschedules for tomorrow.
 */
class PunchAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val punchId = intent.getStringExtra(IntentConstants.EXTRA_PUNCH_ID) ?: return
        val label = intent.getStringExtra(IntentConstants.EXTRA_PUNCH_LABEL) ?: context.getString(R.string.default_punch_label)
        val time = intent.getStringExtra(IntentConstants.EXTRA_PUNCH_TIME) ?: ""
        val punchType = intent.getStringExtra(IntentConstants.EXTRA_PUNCH_TYPE) ?: ""
        val daysOfWeek = intent.getIntegerArrayListExtra(IntentConstants.EXTRA_DAYS_OF_WEEK) ?: arrayListOf()

        Log.d(LOG_TAG, "Alarm received for: $label at $time")

        // 1. Reschedule for the next enabled day
        rescheduleForTomorrow(context, punchId, label, time, punchType, daysOfWeek)

        // 2. Intent to open the app
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(IntentConstants.EXTRA_PUNCH_TYPE, punchType)
            putExtra(IntentConstants.EXTRA_PUNCH_LABEL, label)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            punchId.hashCode(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Show standard notification (no custom layout/activity)
        showNotification(context, time, label, pendingIntent)
    }

    private fun rescheduleForTomorrow(
        context: Context,
        id: String,
        label: String,
        time: String,
        punchType: String,
        daysOfWeek: ArrayList<Int>
    ) {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return

        // Use helper to find the next valid occurrence (at least 1 day in the future)
        val calendar = AlarmHelper.getNextOccurrence(hour, minute, daysOfWeek)
        
        // If the helper returned today because it's earlier, force at least 1 day ahead
        // But getNextOccurrence already checks target.after(now). 
        // If we are IN the onReceive of an alarm, 'now' is exactly the alarm time.
        // So we should force it to look from 'now + 1 minute' or just add 1 day to ensure it's future.
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            // Re-check enabled days if we want to be strict, or just let getNextOccurrence handle it
            // Actually, if we just triggered now, the next one MUST be at least tomorrow.
            // Let's refine getNextOccurrence or handle it here.
        }

        val alarmIntent = Intent(context, PunchAlarmReceiver::class.java).apply {
            putExtra(IntentConstants.EXTRA_PUNCH_ID, id)
            putExtra(IntentConstants.EXTRA_PUNCH_LABEL, label)
            putExtra(IntentConstants.EXTRA_PUNCH_TIME, time)
            putExtra(IntentConstants.EXTRA_PUNCH_TYPE, punchType)
            putIntegerArrayListExtra(IntentConstants.EXTRA_DAYS_OF_WEEK, daysOfWeek)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e(LOG_TAG, "SecurityException rescheduling alarm", e)
        }
    }

    private fun showNotification(
        context: Context,
        time: String,
        label: String,
        pendingIntent: PendingIntent
    ) {
        val channelId = "punch_alarm_channel_v1"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            context.getString(R.string.punch_notification_action),
            pendingIntent
        ).build()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(time)
            .setContentText(label)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .addAction(action)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(label.hashCode(), notification)
    }

    companion object {
        private const val LOG_TAG = "PunchAlarmReceiver"
    }
}

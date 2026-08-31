package com.psildave.punchtheclock.data.local

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.psildave.punchtheclock.shared.model.Reminder

/**
 * Local storage manager for the Wear OS application.
 *
 * Uses SharedPreferences to persist simple data, such as the list of reminders.
 */
object WatchStorage {
    private const val PREF_NAME = "punch_watch_prefs"
    private const val KEY_REMINDERS = "saved_reminders"

    /**
     * Saves the list of reminders to local storage.
     *
     * @param context Application context.
     * @param reminders List of [Reminder] objects to be saved.
     */
    fun saveReminders(context: Context, reminders: List<Reminder>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(reminders)
        prefs.edit { putString(KEY_REMINDERS, json) }
    }

    /**
     * Retrieves the list of saved reminders from local storage.
     *
     * @param context Application context.
     * @return A list of [Reminder] objects. Returns an empty list if no data is found.
     */
    fun getReminders(context: Context): List<Reminder> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_REMINDERS, null) ?: return emptyList()
        val type = object : TypeToken<List<Reminder>>() {}.type
        return Gson().fromJson(json, type)
    }
}
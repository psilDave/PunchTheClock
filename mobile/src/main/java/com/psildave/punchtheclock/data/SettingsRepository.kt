package com.psildave.punchtheclock.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.shared.model.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository responsible for managing application settings, such as user name and reminders.
 *
 * Uses [DataStore] for data persistence.
 */
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    private val gson = Gson()

    /**
     * Flow emitting the current user's name.
     */
    val userNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: context.getString(R.string.default_user_name)
    }

    /**
     * Flow emitting the list of configured reminders.
     * Returns a default list if no reminders are saved.
     */
    val remindersFlow: Flow<List<Reminder>> = dataStore.data.map { preferences ->
        val json = preferences[REMINDERS_LIST]
        if (json.isNullOrEmpty()) {
            listOf(
                Reminder(
                    time = "09:00",
                    label = context.getString(R.string.reminder_label_clock_in),
                    punchType = PunchType.CLOCK_IN.name,
                    isEnabled = true,
                    daysOfWeek = listOf(2, 3, 4, 5, 6)
                ),
                Reminder(
                    time = "12:00",
                    label = context.getString(R.string.reminder_label_lunch_out),
                    punchType = PunchType.LUNCH.name,
                    isEnabled = true,
                    daysOfWeek = listOf(2, 3, 4, 5, 6)
                ),
                Reminder(
                    time = "13:00",
                    label = context.getString(R.string.reminder_label_lunch_return),
                    punchType = PunchType.LUNCH.name,
                    isEnabled = false,
                    daysOfWeek = listOf(2, 3, 4, 5, 6)
                ),
                Reminder(
                    time = "18:00",
                    label = context.getString(R.string.reminder_label_clock_out),
                    punchType = PunchType.CLOCK_OUT.name,
                    isEnabled = true,
                    daysOfWeek = listOf(2, 3, 4, 5, 6)
                )
            )
        } else {
            val type = object : TypeToken<List<Reminder>>() {}.type
            gson.fromJson<List<Reminder>>(json, type)
        }
    }

    /**
     * Saves the list of reminders to persistent storage.
     *
     * @param reminders The list of [Reminder] objects to be saved.
     */
    suspend fun saveReminders(reminders: List<Reminder>) {
        val json = gson.toJson(reminders)
        dataStore.edit { preferences ->
            preferences[REMINDERS_LIST] = json
        }
    }

    /**
     * Saves the user's name to persistent storage.
     *
     * @param newName The new user name.
     */
    suspend fun saveUserName(newName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = newName
        }
    }

    private companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val REMINDERS_LIST = stringPreferencesKey("reminders_list")
    }
}
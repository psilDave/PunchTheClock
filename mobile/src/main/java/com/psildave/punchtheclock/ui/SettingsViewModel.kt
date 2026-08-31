package com.psildave.punchtheclock.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.data.SettingsRepository
import com.psildave.punchtheclock.data.WearSyncManager
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.shared.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val repository: SettingsRepository,
    private val wearSyncManager: WearSyncManager
) : AndroidViewModel(application) {

    val userName: StateFlow<String> = repository.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "User")

    val reminders: StateFlow<List<Reminder>> = repository.remindersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveUserName(name: String) {
        viewModelScope.launch {
            repository.saveUserName(name)
        }
    }

    fun addReminder() {
        val currentList = reminders.value.toMutableList()
        val defaultLabel =
            getApplication<Application>().getString(R.string.settings_new_reminder_default)
        currentList.add(
            Reminder(
                time = "00:00",
                label = defaultLabel,
                punchType = PunchType.OTHER.name,
                isEnabled = true,
            )
        )
        viewModelScope.launch {
            repository.saveReminders(currentList)
            wearSyncManager.syncRemindersToWearable(currentList)
        }
    }

    fun removeReminder(id: String) {
        val currentList = reminders.value.filter { it.id != id }
        viewModelScope.launch {
            repository.saveReminders(currentList)
            wearSyncManager.syncRemindersToWearable(currentList)
        }
    }

    fun updateReminder(updatedReminder: Reminder?) {
        val currentList = reminders.value.map {
            if (it.id == updatedReminder?.id) updatedReminder else it
        }
        viewModelScope.launch {
            repository.saveReminders(currentList)
            wearSyncManager.syncRemindersToWearable(currentList)
        }
    }

    fun forceSyncToWatch() {
        val currentList = reminders.value
        wearSyncManager.syncRemindersToWearable(currentList)
    }
}

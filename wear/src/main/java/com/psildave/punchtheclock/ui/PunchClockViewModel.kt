package com.psildave.punchtheclock.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.psildave.punchtheclock.data.PunchRepository
import com.psildave.punchtheclock.data.helpers.fetchCurrentLocation
import com.psildave.punchtheclock.data.helpers.getCurrentTimeString
import com.psildave.punchtheclock.shared.constants.IntentConstants
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.data.PunchClockUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the PunchTheClock Wear OS application.
 *
 * Manages the UI state and interactions with the repository.
 */
@HiltViewModel
class PunchClockViewModel @Inject constructor(
    private val repository: PunchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialType: PunchType = savedStateHandle.get<String>(IntentConstants.EXTRA_PUNCH_TYPE)?.let {
        try { PunchType.valueOf(it) } catch (_: Exception) { PunchType.CLOCK_IN }
    } ?: PunchType.CLOCK_IN

    private val initialLabel: String = savedStateHandle.get<String>(IntentConstants.EXTRA_PUNCH_LABEL) 
        ?: repository.getDefaultPunchLabel()

    private val _uiState = MutableStateFlow(
        PunchClockUiState(initialType, getCurrentTimeString(), initialLabel)
    )
    val uiState: StateFlow<PunchClockUiState> = _uiState.asStateFlow()

    var selectedType by mutableStateOf(initialType)
        private set

    /**
     * Attempts to send a punch event.
     */
    suspend fun performPunch(fusedLocationClient: FusedLocationProviderClient): Pair<Boolean, String>? {
        val location = fetchCurrentLocation(fusedLocationClient)
        return if (location != null) {
            val punchTime = getCurrentTimeString()
            val success = repository.sendPunch(selectedType, punchTime, location)
            Pair(success, punchTime)
        } else {
            null
        }
    }

    /**
     * Updates the UI state from an external source (e.g., Intent).
     */
    fun updateFromIntent(type: PunchType, label: String) {
        selectedType = type
        _uiState.value = PunchClockUiState(type, getCurrentTimeString(), label)
    }

    /**
     * Updates the selected punch type.
     */
    fun onTypeSelected(newType: PunchType, statusText: String) {
        selectedType = newType
        _uiState.value = PunchClockUiState(newType, getCurrentTimeString(), statusText)
    }

    /**
     * Saves the punch locally when GPS or connection fails.
     */
    fun saveOffline(punchType: String, label: String, successText: String) {
        viewModelScope.launch {
            repository.saveOfflinePunch(punchType, label)
            _uiState.value = PunchClockUiState(selectedType, getCurrentTimeString(), successText)
        }
    }
}

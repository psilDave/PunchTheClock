package com.psildave.punchtheclock.ui

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.data.database.PunchDao
import com.psildave.punchtheclock.data.database.PunchEntity
import com.psildave.punchtheclock.data.getAddressFromCoordinates
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.data.DailyHistory
import com.psildave.punchtheclock.ui.data.PunchClockUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * ViewModel responsible for managing punch record data and the main UI state.
 */
@HiltViewModel
class PunchViewModel @Inject constructor(
    application: Application,
    private val dao: PunchDao,
    private val fusedLocationClient: FusedLocationProviderClient
) : AndroidViewModel(application) {

    val allPunches: StateFlow<List<PunchEntity>> = dao.getAllPunches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val historyData: StateFlow<List<DailyHistory>> = allPunches.map { punches ->
        punches
            .groupBy { formatHeaderDate(it.timestamp) }
            .map { (dateText, punchesForDay) ->
                DailyHistory(
                    dateText = dateText,
                    totalWorkedText = calculateWorkedHours(punchesForDay),
                    punches = punchesForDay.sortedByDescending { it.timestamp }
                )
            }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWorkedHoursState: StateFlow<PunchClockUiState> = allPunches
        .map { punches ->
            val today = LocalDate.now()
            val punchesToday = punches.filter { punch ->
                val punchDate = Instant.ofEpochMilli(punch.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                punchDate == today
            }
            val calculatedText = calculateWorkedHours(punchesToday)

            if (calculatedText == "0m" && punchesToday.isEmpty()) {
                PunchClockUiState.Success(
                    totalWorkedText = "",
                    statusResId = R.string.home_no_punches_today,
                    isTodayEmpty = true
                )
            } else {
                PunchClockUiState.Success(
                    totalWorkedText = calculatedText,
                    statusResId = R.string.home_worked_today,
                    isTodayEmpty = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PunchClockUiState.Loading)


    /**
     * Records a new punch event manually from the mobile app.
     * Includes de-duplication logic and real-time location resolution.
     */
    @SuppressLint("MissingPermission")
    fun onPunchConfirmed(type: PunchType) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            
            // DE-DUPLICATION: Check if the same type exists within a 5-min window
            val windowMillis = TimeUnit.MINUTES.toMillis(5)
            val exists = dao.existsPunchInRange(type.name, now - windowMillis, now + windowMillis)
            
            if (exists) {
                return@launch
            }

            // Fetch current location using standard callback listener (avoiding await for now)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    viewModelScope.launch {
                        val locationLabel = if (location != null) {
                            getAddressFromCoordinates(
                                context = getApplication(),
                                latitude = location.latitude,
                                longitude = location.longitude
                            ) ?: getApplication<Application>().getString(R.string.home_current_location)
                        } else {
                            getApplication<Application>().getString(R.string.home_current_location)
                        }

                        val timeString = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        
                        dao.insertPunch(
                            PunchEntity(
                                punchType = type.name,
                                timeString = timeString,
                                timestamp = now,
                                locationAddress = locationLabel
                            )
                        )
                    }
                }
        }
    }

    private fun calculateWorkedHours(punches: List<PunchEntity>): String {
        val chronological = punches.sortedBy { it.timestamp }
        var totalMillis = 0L

        var lastToggleTime: Long? = null
        var lastValidPunch: PunchEntity? = null

        for (punch in chronological) {
            if (lastValidPunch != null &&
                lastValidPunch.punchType == punch.punchType &&
                (punch.timestamp - lastValidPunch.timestamp) < (1 * 60 * 1000L)
            ) {
                continue
            }

            if (lastToggleTime == null) {
                lastToggleTime = punch.timestamp
            } else {
                totalMillis += (punch.timestamp - lastToggleTime)
                lastToggleTime = null
            }

            lastValidPunch = punch
        }

        if (lastToggleTime != null) {
            val isToday = Instant.ofEpochMilli(lastToggleTime).atZone(ZoneId.systemDefault())
                .toLocalDate() == LocalDate.now()
            if (isToday) {
                totalMillis += (System.currentTimeMillis() - lastToggleTime)
            }
        }

        val totalMinutes = totalMillis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return if (hours > 0) {
            getApplication<Application>().getString(R.string.time_unit_hour_short, hours, minutes)
        } else {
            getApplication<Application>().getString(R.string.time_unit_minute_short, minutes)
        }
    }

    private fun formatHeaderDate(timestamp: Long): String {
        val context = getApplication<Application>()
        val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> context.getString(R.string.history_today)
            yesterday -> context.getString(R.string.history_yesterday)
            else -> {
                val pattern = context.getString(R.string.history_date_format)
                date.format(DateTimeFormatter.ofPattern(pattern))
            }
        }
    }
}

package com.psildave.punchtheclock.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.psildave.punchtheclock.data.database.PunchDao
import com.psildave.punchtheclock.data.database.PunchEntity
import com.psildave.punchtheclock.shared.constants.DataLayerConstants
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Service that listens for messages from the Wearable Data Layer.
 */
@AndroidEntryPoint
class WearablePunchListenerService : WearableListenerService() {

    @Inject
    lateinit var punchDao: PunchDao
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path == DataLayerConstants.PUNCH_EVENT_PATH) {
            val payload = String(messageEvent.data, Charsets.UTF_8)
            Log.d(LOG_TAG, "Received punch event: $payload")

            val dataChunks = payload.split(",")

            if (dataChunks.size >= 4) {
                val punchType = dataChunks[0]
                val timeLabel = dataChunks[1]
                val lat = dataChunks[2].toDoubleOrNull() ?: 0.0
                val lng = dataChunks[3].toDoubleOrNull() ?: 0.0

                // If it's an offline punch, it might have the timestamp in the timeLabel
                val timestamp = if (timeLabel.startsWith("Offline: ")) {
                    timeLabel.removePrefix("Offline: ").toLongOrNull() ?: System.currentTimeMillis()
                } else {
                    System.currentTimeMillis()
                }

                serviceScope.launch {
                    // DE-DUPLICATION LOGIC:
                    // Check if a punch of the same type was recorded within a 5-minute window
                    val windowMillis = TimeUnit.MINUTES.toMillis(5)
                    val duplicateExists = punchDao.existsPunchInRange(
                        type = punchType,
                        startTime = timestamp - windowMillis,
                        endTime = timestamp + windowMillis
                    )

                    if (duplicateExists) {
                        Log.w(LOG_TAG, "Duplicate punch detected for type $punchType within 5-min window. Ignoring.")
                        return@launch
                    }

                    val resolvedAddress = getAddressFromCoordinates(
                        context = application,
                        latitude = lat,
                        longitude = lng
                    )

                    punchDao.insertPunch(
                        PunchEntity(
                            punchType = punchType,
                            timeString = timeLabel, // Keep the original time label
                            locationAddress = resolvedAddress ?: "Localização resolvida",
                            timestamp = timestamp
                        )
                    )

                    Log.d(LOG_TAG, "Record saved to local database successfully!")
                }
            }
        }
    }

    companion object {
        private const val LOG_TAG = "WearablePunchListenerService"
    }
}

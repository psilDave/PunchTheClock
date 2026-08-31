package com.psildave.punchtheclock.data

import android.content.Context
import android.location.Location
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.data.helpers.sendPunchToMobile
import com.psildave.punchtheclock.data.local.WatchStorage
import com.psildave.punchtheclock.data.local.dao.OfflinePunchDao
import com.psildave.punchtheclock.data.local.entity.OfflinePunchEntity
import com.psildave.punchtheclock.data.worker.SyncWorker
import com.psildave.punchtheclock.shared.model.PunchType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that abstracts data operations for the Wear OS app.
 */
@Singleton
class PunchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val offlinePunchDao: OfflinePunchDao
) {

    /**
     * Attempts to send a punch event to the mobile device.
     */
    suspend fun sendPunch(
        punchType: PunchType,
        time: String,
        location: Location,
    ): Boolean {
        return sendPunchToMobile(
            context = context,
            punchType = punchType.name,
            time = time,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    /**
     * Persists a punch event locally and schedules a synchronization worker.
     */
    suspend fun saveOfflinePunch(punchType: String, label: String) {
        offlinePunchDao.insertPunch(
            OfflinePunchEntity(
                punchType = punchType,
                label = label
            )
        )
        scheduleSync()
    }

    /**
     * Schedules the background sync worker with network constraints.
     */
    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "offline_punch_sync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /**
     * Returns a localized default label for the punch action.
     */
    fun getDefaultPunchLabel(): String {
        return context.getString(R.string.next_action_pending)
    }

    /**
     * Retrieves saved reminders from local storage.
     */
    fun getReminders() = WatchStorage.getReminders(context)
}

package com.psildave.punchtheclock.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.Wearable
import com.psildave.punchtheclock.data.helpers.sendPunchToMobile
import com.psildave.punchtheclock.data.local.dao.OfflinePunchDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

private const val TAG = "SyncWorker"

/**
 * Background worker responsible for synchronizing offline punches with the mobile device.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val offlinePunchDao: OfflinePunchDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting synchronization check...")

        val nodeClient = Wearable.getNodeClient(applicationContext)
        val connectedNodes = try {
            nodeClient.connectedNodes.await()
        } catch (_: Exception) {
            emptyList()
        }

        if (connectedNodes.isEmpty()) {
            Log.d(TAG, "No nodes connected (phone not reachable). Retrying later...")
            return Result.retry()
        }

        val pendingPunches = offlinePunchDao.getAllOfflinePunches()
        if (pendingPunches.isEmpty()) {
            Log.d(TAG, "No pending punches to sync.")
            return Result.success()
        }

        var allSuccess = true

        for (punch in pendingPunches) {
            val success = sendPunchToMobile(
                context = applicationContext,
                punchType = punch.punchType,
                time = "Offline: ${punch.timestamp}", // Using a label to indicate offline origin
                latitude = 0.0, // GPS is usually unavailable for offline punches
                longitude = 0.0
            )

            if (success) {
                Log.d(TAG, "Successfully synced punch ID: ${punch.id}")
                offlinePunchDao.deletePunch(punch.id)
            } else {
                Log.e(TAG, "Failed to sync punch ID: ${punch.id}. Will retry later.")
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}

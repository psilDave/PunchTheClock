package com.psildave.punchtheclock.data.helpers

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

private const val LOG_TAG = "LocationHelper"

/**
 * Fetches the current high-accuracy location from the FusedLocationProviderClient.
 *
 * @param fusedLocationClient The client to request location from.
 * @return The current [Location] or null if the request fails or permission is missing.
 */
@SuppressLint("MissingPermission")
suspend fun fetchCurrentLocation(fusedLocationClient: FusedLocationProviderClient): Location? {
    Log.d(LOG_TAG, "fetchCurrentLocation")
    return try {
        val cancellationToken = CancellationTokenSource().token
        return fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, cancellationToken
        ).await()
    } catch (e: Exception) {
        Log.d(LOG_TAG, "Error to get location: ${e.message}")
        null
    }
}

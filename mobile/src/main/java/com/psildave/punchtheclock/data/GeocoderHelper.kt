package com.psildave.punchtheclock.data

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

private const val LOG_TAG = "GeocoderHelper"


/**
 * Suspended function to retrieve a human-readable address from geographical coordinates.
 *
 * @param context Application context used to initialize the [Geocoder].
 * @param latitude Latitude coordinate.
 * @param longitude Longitude coordinate.
 * @return A formatted address string, or null if the address cannot be retrieved.
 */
suspend fun getAddressFromCoordinates(
    context: Context,
    latitude: Double,
    longitude: Double
): String? {
    return withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val result = addresses.firstOrNull()?.let { address ->
                            getFormatted(address)
                        }
                        continuation.resume(result)
                    }
                }
            } else {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { address ->
                    getFormatted(address)
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error getting address: ${e.message}")
            null
        }
    }
}

/**
 * Formats an [Address] object into a comma-separated string.
 *
 * @param address The [Address] object returned by the Geocoder.
 * @return A formatted string containing street, number, city, state, and country.
 */
private fun getFormatted(address: Address): String {
    val number = address.subThoroughfare ?: ""
    val street = address.thoroughfare ?: ""
    val city = address.locality ?: address.subAdminArea ?: ""
    val state = address.adminArea ?: ""
    val country = address.countryName ?: address.countryCode ?: ""
    return "$number, $street, $city, $state, $country".trim(',', ' ')
}



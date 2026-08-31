package com.psildave.punchtheclock.data.helpers

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import com.psildave.punchtheclock.shared.constants.DataLayerConstants
import kotlinx.coroutines.tasks.await

private const val LOG_TAG = "DataLayerHelper"

/**
 * Sends a punch event from the Wear OS device to the connected mobile device.
 *
 * @param context Application context.
 * @param punchType The type of punch (e.g., CLOCK_IN).
 * @param time The time of the punch.
 * @param latitude Latitude coordinate.
 * @param longitude Longitude coordinate.
 * @return True if the message was sent successfully to at least one node, false otherwise.
 */
suspend fun sendPunchToMobile(
    context: Context,
    punchType: String,
    time: String,
    latitude: Double,
    longitude: Double
): Boolean {
    return try {
        Log.d(LOG_TAG, "sendPunchToMobile")
        val nodeClient = Wearable.getNodeClient(context)
        val messageClient = Wearable.getMessageClient(context)

        val nodes = nodeClient.connectedNodes.await()
        if (nodes.isEmpty()) {
            Log.e(LOG_TAG, "No connected nodes found.")
            return false
        }

        val payloadString = "$punchType,$time,$latitude,$longitude"
        val payloadBytes = payloadString.toByteArray(Charsets.UTF_8)

        val nodeId = nodes.first().id
        messageClient.sendMessage(nodeId, DataLayerConstants.PUNCH_EVENT_PATH, payloadBytes).await()
        Log.d(LOG_TAG, "Punch sent successfully to node: $nodeId")
        true
    } catch (e: Exception) {
        Log.e(LOG_TAG, "Error sending punch: ${e.message}")
        false
    }
}

package com.psildave.punchtheclock.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.psildave.punchtheclock.shared.constants.DataLayerConstants
import com.psildave.punchtheclock.shared.model.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manager responsible for synchronizing data from the mobile device to the wearable.
 *
 * It uses the Wearable Data Client to push reminder updates to the connected watch.
 */
@Singleton
class WearSyncManager @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val dataClient = Wearable.getDataClient(context)
    private val gson = Gson()

    /**
     * Synchronizes a list of reminders to the wearable device.
     *
     * Converts the list to JSON and puts it into the Wearable Data Layer.
     * Includes a timestamp to force an update even if the data content hasn't changed.
     *
     * @param reminders The list of [Reminder] objects to synchronize.
     */
    fun syncRemindersToWearable(reminders: List<Reminder>) {
        val json = gson.toJson(reminders)

        val putDataReq = PutDataMapRequest.create(DataLayerConstants.REMINDERS_PATH).run {
            dataMap.putString(DataLayerConstants.KEY_REMINDERS_JSON, json)

            // O TRUQUE: O DataClient só envia se houver MUDANÇA nos dados.
            // Colocar um timestamp garante que o relógio sempre receba o alerta,
            // mesmo que você só esteja forçando uma sincronização manual.
            dataMap.putLong(DataLayerConstants.KEY_TIMESTAMP, System.currentTimeMillis())

            asPutDataRequest()
        }

        dataClient.putDataItem(putDataReq).addOnSuccessListener {
            Log.d(LOG_TAG, "Synchronization sent successfully: $json")
        }.addOnFailureListener { e ->
            Log.e(LOG_TAG, "Failed to send to watch", e)
        }
    }

    companion object {
        private const val LOG_TAG = "WearSyncManager"
    }
}

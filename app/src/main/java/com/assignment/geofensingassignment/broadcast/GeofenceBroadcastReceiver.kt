package com.assignment.geofensingassignment.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

private const val TAG = "GeofenceBroadcast"

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        Log.d(TAG, "invoke into onReceive.")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofence = geofencingEvent.triggeringGeofences

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d(TAG, "geofenceTransition: ${triggeringGeofence[0]}")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d(TAG, "geofenceTransition: ${triggeringGeofence[0]}")

            }
            else -> {
                Log.e(TAG, "Invalid type transition $geofenceTransition")
            }
        }
    }

}
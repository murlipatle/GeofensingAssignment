package com.assignment.geofencing.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.assignment.geofencing.model.UserData
import com.assignment.geofencing.repository.UserDataRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val tag = this.javaClass.simpleName

    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "invoke into onReceive.")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(tag, errorMessage)
            return
        }
        val geofenceTransition = geofencingEvent.geofenceTransition
        val currentTime: Date = Calendar.getInstance().time
        val triggeringGeofence = geofencingEvent.triggeringGeofences

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d(tag, "onReceive: ${triggeringGeofence[0]}")
                userDataRepository.addUserData(UserData(triggeringGeofence[0].requestId,currentTime.toString(),true,""))
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                userDataRepository.addUserData(UserData(triggeringGeofence[0].requestId,"",false,currentTime.toString()))

            }
            else -> {
                Log.e(tag, "Invalid type transition $geofenceTransition")
            }
        }
    }

}
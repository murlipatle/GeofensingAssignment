package com.assignment.geofencing.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.assignment.geofencing.model.UserData
import com.assignment.geofencing.repository.UserDataRepository
import com.assignment.geofencing.utils.GeofenceConst
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val tag = this.javaClass.simpleName

    @Inject
    lateinit var userDataRepository: UserDataRepository
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "invoke into onReceive.")
        firebaseAnalytics = Firebase.analytics

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            val params = Bundle()
            params.putString("Status", errorMessage)
            firebaseAnalytics.logEvent("GEOFENCE_TRANSITION", params)
            Log.e(tag, errorMessage)
            return
        }
        val geofenceTransition = geofencingEvent.geofenceTransition
        val currentTime: Date = Calendar.getInstance().time
        val triggeringGeofence = geofencingEvent.triggeringGeofences

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d(tag, "onReceive: ${triggeringGeofence[0]}")
                val params = Bundle()
                params.putString("Location","${GeofenceConst.LATITUDE}, ${GeofenceConst.LONGITUDE}")
                params.putString("Status", "GEOFENCE_TRANSITION_ENTER")
                params.putString("RequestId", triggeringGeofence[0].requestId)
                params.putString("Time", currentTime.toString())
                firebaseAnalytics.logEvent("GEOFENCE_TRANSITION", params)
                userDataRepository.addUserData(UserData(triggeringGeofence[0].requestId,currentTime.toString(),true,""))
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val params = Bundle()
                params.putString("Location","${GeofenceConst.LATITUDE}, ${GeofenceConst.LONGITUDE}")
                params.putString("Status", "GEOFENCE_TRANSITION_EXIT")
                params.putString("RequestId", triggeringGeofence[0].requestId)
                params.putString("Time", currentTime.toString())
                firebaseAnalytics.logEvent("GEOFENCE_TRANSITION", params)
                userDataRepository.addUserData(UserData(triggeringGeofence[0].requestId,"",false,currentTime.toString()))

            }
            else -> {
                Log.e(tag, "Invalid type transition $geofenceTransition")
            }
        }
    }

}
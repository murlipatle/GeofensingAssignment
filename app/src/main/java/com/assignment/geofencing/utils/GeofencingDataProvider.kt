package com.assignment.geofencing.utils

import com.assignment.geofencing.utils.GeofenceConst.Companion.GEOFENCING_RANGE_RADIUS
import com.assignment.geofencing.utils.GeofenceConst.Companion.LATITUDE
import com.assignment.geofencing.utils.GeofenceConst.Companion.LONGITUDE
import com.google.android.gms.location.Geofence

class GeofencingDataProvider {
    companion object {
        fun getGeofenceList(): ArrayList<Geofence> {
            val geofenceList = ArrayList<Geofence>()
            geofenceList.add(
                Geofence.Builder()
                    .setRequestId("RequestId")
                    .setCircularRegion(LATITUDE, LONGITUDE, GEOFENCING_RANGE_RADIUS)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
            return geofenceList
        }
    }
}
package com.assignment.geofensingassignment

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.assignment.geofensingassignment.broadcast.GeofenceBroadcastReceiver
import com.assignment.geofensingassignment.databinding.ActivityGeofencingBinding
import com.assignment.geofensingassignment.utils.Constants.Companion.BACKGROUND_LOCATION_PERMISSION_INDEX
import com.assignment.geofensingassignment.utils.Constants.Companion.LOCATION_PERMISSION_INDEX
import com.assignment.geofensingassignment.utils.Constants.Companion.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.assignment.geofensingassignment.utils.Constants.Companion.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.assignment.geofensingassignment.utils.Constants.Companion.REQUEST_TURN_DEVICE_LOCATION_ON
import com.assignment.geofensingassignment.utils.GeofenceConst.Companion.LATITUDE
import com.assignment.geofensingassignment.utils.GeofenceConst.Companion.LOGNTITUTE
import com.assignment.geofensingassignment.utils.GeofencingDataProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GeofencingActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName

    private lateinit var binding: ActivityGeofencingBinding
    lateinit var geofencingClient: GeofencingClient


    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeofencingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        geofencingClient = LocationServices.getGeofencingClient(this)
        binding.button.setOnClickListener {
            if (binding.button.text.toString().equals(getString(R.string.add_geofencing),true)) {
                addGeofence()
            }else{
                removeGeofence()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsAndStartGeofencing()
    }

    /**
     * Check permission for Geofence update .
     */
    private fun checkPermissionsAndStartGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    /*
    *  Uses the Location Client to check the current state of location settings, and gives the user
    *  the opportunity to turn on location services within our app.
    */
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@GeofencingActivity,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.permission_title))
                    .setMessage(resources.getString(R.string.supporting_text_permission))
                    .setNeutralButton(getString(R.string.exit)) { dialog, which ->
                        finish()
                    }
                    .setPositiveButton(getString(R.string.setting)) { dialog, which ->
                        checkDeviceLocationSettingsAndStartGeofence()
                    }
                    .show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                addGeofence()
            }
        }
    }
    //adding a geofence
    @SuppressLint("MissingPermission")
    private fun addGeofence(){
            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
                .run {
                    addOnSuccessListener {
                        // Geofences added
                        Log.d("TAG", "addOnSuccessListener: ")
                        binding.textView.text =
                            "Geofences added on \n Lat:${LATITUDE},Long:${LOGNTITUTE}"
                        binding.button.text=getString(R.string.remove_geofence)
                    }
                    addOnFailureListener {
                        // Failed to add geofences
                        Log.d("TAG", "addOnFailureListener:$it ")
                        binding.textView.text =
                            "Failed to add geofence $it"

                    }
                }
    }
    //removing a geofence
    private fun removeGeofence(){
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "Removed Geofence")
                binding.textView.text = "Press Add Button to add geofencing on \n Lat:${LATITUDE},Long:${LOGNTITUTE}"
                binding.button.text=getString(R.string.add_geofencing)

            }
            addOnFailureListener {
                Log.d(TAG, "failed to remove Geofence: ")
            }
        }
    }
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(GeofencingDataProvider.getGeofenceList())
        }.build()
    }
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
    /*
    *  Determines whether the app has the appropriate permissions across Android 10+ and all other
    *  Android versions.
    */
    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    /*
    *  Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION.
    */
    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return

        // Else request the permission
        // this provides the result[LOCATION_PERMISSION_INDEX]
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            runningQOrLater -> {
                // this provides the result[BACKGROUND_LOCATION_PERMISSION_INDEX]
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

        Log.d(TAG, "Request foreground only location permission")
        ActivityCompat.requestPermissions(
            this@GeofencingActivity,
            permissionsArray,
            resultCode
        )
    }

    /*
   *  When we get the result from asking the user to turn on device location, we call
   *  checkDeviceLocationSettingsAndStartGeofence again to make sure it's actually on, but
   *  we don't resolve the check to keep the user from seeing an endless loop.
   */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            // We don't rely on the result code, but just check the location setting again
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }


    /*
     * In all cases, we need to have the location permission.  On Android 10+ (Q) we need to have
     * the background permission as well.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)



        Log.d(TAG, "onRequestPermissionResult")

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.permission_title))
                .setMessage(resources.getString(R.string.supporting_text))
                .setNeutralButton(getString(R.string.exit)) { dialog, which ->
                    finish()
                }
                .setPositiveButton(getString(R.string.setting)) { dialog, which ->
                    // Displays App settings screen.
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
                .show()

        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

}
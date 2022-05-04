package com.assignment.geofensingassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.assignment.geofensingassignment.databinding.ActivityGeofencingBinding

class GeofencingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeofencingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeofencingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
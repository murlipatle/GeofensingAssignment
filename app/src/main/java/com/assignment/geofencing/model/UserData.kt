package com.assignment.geofencing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val requestId: String,
    val entryTime: String,
    val isEntry: Boolean,
    val exitTime: String
) {
    constructor(requestId: String, entryTime: String, isEntry: Boolean, exitTime: String) : this(
        0,
        requestId,
        entryTime,
        isEntry,
        exitTime
    )

}
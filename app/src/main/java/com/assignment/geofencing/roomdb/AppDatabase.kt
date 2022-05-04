package com.assignment.geofencing.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assignment.geofencing.model.UserData
import com.assignment.geofencing.roomdb.dao.UserDataDao

@Database(entities = [UserData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao
}
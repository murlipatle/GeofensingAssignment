package com.assignment.geofencing.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.assignment.geofencing.model.UserData

@Dao
interface UserDataDao {

    @Query("SELECT * FROM UserData")
    fun getAllData(): LiveData<List<UserData>>

    @Insert
    fun insertUserData(userData: UserData)
}
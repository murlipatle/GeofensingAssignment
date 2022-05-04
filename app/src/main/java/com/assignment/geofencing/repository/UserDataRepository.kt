package com.assignment.geofencing.repository

import androidx.lifecycle.LiveData
import com.assignment.geofencing.model.UserData
import com.assignment.geofencing.roomdb.dao.UserDataDao
import javax.inject.Inject

class UserDataRepository @Inject constructor(private val userDataDao: UserDataDao) {
    fun addUserData(userData: UserData) {
        userDataDao.insertUserData(userData)
    }

    fun fetchUserDataList(): LiveData<List<UserData>> {
        return userDataDao.getAllData()
    }

}
package com.assignment.geofencing.viewmodel

import androidx.lifecycle.ViewModel
import com.assignment.geofencing.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GeofencingViewModel @Inject constructor(private val userDataRepository: UserDataRepository) :
    ViewModel() {
    fun getUserDataList() = userDataRepository.fetchUserDataList()

}
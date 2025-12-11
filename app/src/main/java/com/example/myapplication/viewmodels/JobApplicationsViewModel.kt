package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.JobApplication
import com.example.myapplication.network.NetworkModule
import kotlinx.coroutines.launch

class JobApplicationViewModel : ViewModel() {

    private val _applications = MutableLiveData<List<JobApplication>>()
    val applications: LiveData<List<JobApplication>> = _applications

    fun getApplications(userId: Int) : LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                var applications = NetworkModule.apiService.getJobApplications(userId)
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!",e.message.toString())
            }
        }
        return applications
    }
}
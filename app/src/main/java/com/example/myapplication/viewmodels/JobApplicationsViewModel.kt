package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.models.JobApplication
import com.example.core.network.NetworkModule
import kotlinx.coroutines.launch

class JobApplicationViewModel : ViewModel() {

    private val _applications = MutableLiveData<List<JobApplication>>()
    val applications: LiveData<List<JobApplication>> = _applications

    private val _uploadState = MutableLiveData<String>()
    var uploadState : LiveData<String> = _uploadState

    fun getApplicationsForStudent(userId: Int) : LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                val applications = NetworkModule.apiService.getJobApplicationsForStudent(userId)
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!",e.message.toString())
            }
        }
        return applications
    }

    fun getApplicationsForEmployer(employerId: Int) : LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                val applications = NetworkModule.apiService.getJobApplicationsForEmployer(employerId)
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!",e.message.toString())
            }
        }
        return applications
    }

    //iskoristi kasnije za dohvat prijava za specifičan posao
    fun getApplicationsForJob(jobId: Int) : LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                val applications = NetworkModule.apiService.getJobApplicationsForJob(jobId)
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!",e.message.toString())
            }
        }
        return applications
    }

    fun uploadApplication(application: JobApplication) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.postApplication(application)
                Log.d("odgovor", response.raw().toString())
                if (response.isSuccessful) {
                    _uploadState.postValue("Uspješno dodana prijava")
                } else {
                    _uploadState.postValue("Greška pri dodavanju prijave - ${response.code()}")
                }
            } catch (e: Exception) {
                _uploadState.postValue("Greška pri dodavanju prijave - ${e.message}")
            }
        }
    }
}

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

    // ZA LISTE
    private val _applications = MutableLiveData<List<JobApplication>>()
    val applications: LiveData<List<JobApplication>> = _applications

    //ZA POJEDINAČNE
    private val _application = MutableLiveData<JobApplication?>()
    val application: LiveData<JobApplication?> = _application

    private val _uploadState = MutableLiveData<String>()
    var uploadState: LiveData<String> = _uploadState

    private val _uploadCode = MutableLiveData<Int>()
    var uploadCode: LiveData<Int> = _uploadCode
    fun getApplicationsForStudent(userId: Int): LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                val applications = NetworkModule.apiService.getJobApplicationsForStudent(userId)
                Log.d("aplika", applications.toString())
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!", e.message.toString())
            }
        }
        return applications
    }

    fun getApplicationsForEmployer(employerId: Int): LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                val applications = NetworkModule.apiService.getJobApplicationsForEmployer(employerId)
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!", e.message.toString())
            }
        }
        return applications
    }

    //iskoristi kasnije za dohvat prijava za specifičan posao
    fun getApplicationsForJob(jobId: Int): LiveData<List<JobApplication>> {
        viewModelScope.launch {
            try {
                val applications = NetworkModule.apiService.getJobApplicationsForJob(jobId)
                _applications.postValue(applications)
            } catch (e: Exception) {
                _applications.postValue(emptyList())
                Log.d("Debugiranje!", e.message.toString())
            }
        }
        return applications
    }

    fun uploadApplication(application: JobApplication) {
        viewModelScope.launch {
            try {
                Log.d("aplikacija na posao", application.toString())
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

    fun editApplication(application: JobApplication) {
        viewModelScope.launch {
            try {
                Log.d("aplikacija na posao", application.toString())
                val response = NetworkModule.apiService.putApplication(application.id,application)
                Log.d("odgovor", response.raw().toString())
                if (response.isSuccessful) {
                    _uploadState.postValue("Uspješno promjenjena prijava")
                    _uploadCode.postValue(response.code())
                } else {
                    _uploadState.postValue("Greška pri promjeni prijave - ${response.code()}")
                    _uploadCode.postValue(response.code())
                }
            } catch (e: Exception) {
                _uploadState.postValue("Greška pri promjeni prijave - ${e.message}")
            }
        }
    }

    fun getApplicationsById(applicationId: Int): LiveData<JobApplication?> {
        viewModelScope.launch {
            try {
                val application = NetworkModule.apiService.getApplication(applicationId)
                _application.postValue(application)
            } catch (e: Exception) {
                _application.postValue(null)
                Log.d("Debugiranje dohvata!", e.message.toString())
            }
        }
        return application
    }

    fun deleteApplication(applicationId: Int) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.deleteApplication(applicationId)
                if (response.isSuccessful) {
                    _uploadState.postValue("Uspješno obrisana prijava")
                    _uploadCode.postValue(response.code())
                } else {
                    _uploadState.postValue("Greška pri brisanju prijave - ${response.code()}")
                    _uploadCode.postValue(response.code())
                }
            } catch (e: Exception) {
                _uploadState.postValue("Greška pri brisanju prijave - ${e.message}")
                _uploadCode.postValue(-1)
                Log.d("DeleteApp", "Exception while deleting: ${e.message}")
            }
        }
    }
}

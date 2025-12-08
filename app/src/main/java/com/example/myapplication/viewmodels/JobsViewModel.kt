package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.JobListing
import com.example.myapplication.network.NetworkModule
import kotlinx.coroutines.launch

class JobsViewModel : ViewModel() {

    private val _jobs = MutableLiveData<List<JobListing>>()
    val jobs: LiveData<List<JobListing>> = _jobs

    private val _uploadState = MutableLiveData<String>()
    var uploadState: LiveData<String> = _uploadState

    // NEW: State for update operations
    private val _updateState = MutableLiveData<String>()
    val updateState: LiveData<String> = _updateState

    // NEW: Track selected job for editing
    private val _selectedJob = MutableLiveData<JobListing?>()
    val selectedJob: LiveData<JobListing?> = _selectedJob

    // NEW: Track if there are unsaved changes
    private val _hasUnsavedChanges = MutableLiveData<Boolean>(false)
    val hasUnsavedChanges: LiveData<Boolean> = _hasUnsavedChanges

    fun uploadJob(job: JobListing) {
        Log.d("pregled joba", job.toString())
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.postJob(job)
                Log.d("odgovor", response.raw().toString())
                if (response.isSuccessful) {
                    _uploadState.postValue("Uspješno dodan oglas")
                } else {
                    _uploadState.postValue("Greška pri dodavanju oglasa - ${response.code()}")
                }
            } catch (e: Exception) {
                _uploadState.postValue("Greška pri dodavanju oglasa - ${e.message}")
            }
        }
    }

    // NEW: Update existing job
    fun updateJob(job: JobListing) {
        Log.d("update joba", job.toString())
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.updateJob(job.id, job)
                Log.d("update odgovor", response.raw().toString())
                if (response.isSuccessful) {
                    val wasUpdated = response.body() ?: false
                    if (wasUpdated) {
                        _updateState.postValue("Oglas uspješno ažuriran")
                        _hasUnsavedChanges.postValue(false)
                    } else {
                        _updateState.postValue("Greška pri ažuriranju oglasa - backend vratio false")
                    }
                } else {
                    _updateState.postValue("Greška pri ažuriranju oglasa - ${response.code()}")
                }
            } catch (e: Exception) {
                _updateState.postValue("Greška pri ažuriranju oglasa - ${e.message}")
                Log.e("JobsViewModel", "Update error", e)
            }
        }
    }

    // NEW: Set selected job for editing
    fun selectJob(job: JobListing) {
        _selectedJob.value = job
    }

    // NEW: Clear selected job
    fun clearSelectedJob() {
        _selectedJob.value = null
        _hasUnsavedChanges.value = false
    }

    // NEW: Mark that changes have been made
    fun markUnsavedChanges(hasChanges: Boolean) {
        _hasUnsavedChanges.value = hasChanges
    }

    // NEW: Fetch all jobs (for refresh after update)
    fun fetchJobs() {
        viewModelScope.launch {
            try {
                val result = NetworkModule.apiService.getJobs()
                _jobs.postValue(result)
            } catch (e: Exception) {
                Log.e("JobsViewModel", "Error fetching jobs", e)
            }
        }
    }
}
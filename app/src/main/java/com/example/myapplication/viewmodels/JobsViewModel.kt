package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.models.JobListing
import com.example.core.network.NetworkModule
import kotlinx.coroutines.launch


class JobsViewModel : ViewModel() {

    private val _jobs = MutableLiveData<List<JobListing>>()
    val jobs: LiveData<List<JobListing>> = _jobs

    private val _uploadState = MutableLiveData<String>()
    var uploadState : LiveData<String> = _uploadState
    fun uploadJob(job: JobListing) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.postJob(job)
                Log.d("odgovor", response.raw().toString())
                if(response.isSuccessful){
                    _uploadState.postValue("Uspješno dodan oglas")
                }
                else{
                    _uploadState.postValue("Greška pri dodavanju oglasa - ${response.code()}")
                }
            }catch (e: Exception){
                _uploadState.postValue("Greška pri dodavanju oglasa - ${e.message}")
            }
        }
    }
    fun getJobs2(): LiveData<List<JobListing>> {
        viewModelScope.launch {
            try {
                val jobs = NetworkModule.apiService.getJobs()
                if(jobs != null) {
                    _jobs.postValue(jobs)
                }
                else
                {
                    _jobs.postValue(emptyList())
                    Log.d("Debugiranje!", "Dohvaćeni poslovi su null")
                }
            } catch (e: Exception) {
                _jobs.postValue(emptyList())
                Log.d("Debugiranje!", e.message.toString())
            }
        }
        return jobs
    }
}

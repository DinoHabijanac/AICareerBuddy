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
    var uploadState : String = ""
    fun uploadJob(job: JobListing) {
        Log.d("pregled joba",job.toString())
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.postJob(job)
                Log.d("odgovor", response.raw().toString())
                if(response.isSuccessful){
                    uploadState = "Uspješno dodan oglas"
                }
                else{
                    uploadState = "Greška pri dodavanju oglasa - ${response.code()}"
                }
            }catch (e: Exception){
                uploadState = "Greška pri dodavanju oglasa - ${e.message}"
            }
        }
    }
}

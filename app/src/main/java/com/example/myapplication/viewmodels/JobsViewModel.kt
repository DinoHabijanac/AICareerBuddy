package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.models.JobListing
import com.example.core.models.Student
import com.example.core.network.NetworkModule
import kotlinx.coroutines.launch


class JobsViewModel : ViewModel() {

    private val _job = MutableLiveData<JobListing>()
    val job: LiveData<JobListing> = _job

    private val _student = MutableLiveData<Student>()
    val student: LiveData<Student> = _student

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

    fun getJobById(jobId: Int)  {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.getJob(jobId)
                Log.d("dobavljanje posla", response.toString())
                _uploadState.postValue("Uspješno dohvaćen posao")
                _job.postValue(response)
            }catch (e: Exception){
                _uploadState.postValue("Greška pri dohvaćanju oglasa - ${e.message}")
            }
        }
    }

    fun getStudentById(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.getStudent(studentId)
                Log.d("odgovor", response.toString())
                _student.postValue(response)
            } catch (e: Exception) {
                _uploadState.postValue("Greška pri dohvaćanju studenta - ${e.message}")
            }
        }
    }
}

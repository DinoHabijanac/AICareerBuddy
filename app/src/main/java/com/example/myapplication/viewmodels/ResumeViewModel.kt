package com.example.myapplication.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.NetworkModule
import com.example.core.helpers.uriToMultipart
import com.example.core.models.ResumeAIFeedback
import com.example.core.models.ResumeImprovements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

sealed class DeleteState {
    object Idle : DeleteState()
    object Deleting : DeleteState()
    data class Success(val message: String) : DeleteState()
    data class Error(val message: String) : DeleteState()
}

class UploadViewModel : ViewModel() {
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _aiFeedback = MutableStateFlow<ResumeAIFeedback?>(null)
    val aiFeedback: StateFlow<ResumeAIFeedback?> = _aiFeedback

    private val _improvements = MutableStateFlow<ResumeImprovements?>(null)
    val improvements: StateFlow<ResumeImprovements?> = _improvements

    private val _improvementsLoading = MutableStateFlow(false)
    val improvementsLoading: StateFlow<Boolean> = _improvementsLoading

    private val _improvementsError = MutableStateFlow<String?>(null)
    val improvementsError: StateFlow<String?> = _improvementsError

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState

    fun uploadResume(context: Context, uri: Uri, userId: Int) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            try {
                val part = withContext(Dispatchers.IO) {
                    uriToMultipart(context, uri, "file")
                }

                val userIdReqBody = userId.toString().toRequestBody(MultipartBody.FORM)
                Log.d("UploadViewModel", "userIdReqBody: $userIdReqBody")

                val response = NetworkModule.apiService.uploadResume(part, userIdReqBody)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uploadState.value = UploadState.Success(body.message ?: "Uspješno učitano")
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                    _uploadState.value = UploadState.Error(err)
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Neuspjeh pri uploadu")
            }
        }
    }

    fun updateResume(context: Context, uri: Uri, userId: Int) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            try {
                val part = withContext(Dispatchers.IO) {
                    uriToMultipart(context, uri, "file")
                }

                val response = NetworkModule.apiService.updateResume(userId, part)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uploadState.value = UploadState.Success(body.message ?: "Životopis uspješno ažuriran")
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                    _uploadState.value = UploadState.Error(err)
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Neuspjeh pri ažuriranju")
                Log.e("UploadViewModel", "Update error", e)
            }
        }
    }

    fun deleteResume(userId: Int) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Deleting
            try {
                val response = NetworkModule.apiService.deleteResume(userId)

                if (response.isSuccessful) {
                    _deleteState.value = DeleteState.Success("Životopis uspješno obrisan")
                    _uploadState.value = UploadState.Idle
                } else {
                    val err = when (response.code()) {
                        404 -> "Životopis nije pronađen"
                        else -> "Greška pri brisanju: HTTP ${response.code()}"
                    }
                    _deleteState.value = DeleteState.Error(err)
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.message ?: "Neuspjeh pri brisanju")
                Log.e("UploadViewModel", "Delete error", e)
            }
        }
    }

    fun reset() {
        _uploadState.value = UploadState.Idle
        _deleteState.value = DeleteState.Idle
    }

    fun analyzeResume(userId: Int) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.analyzeResumeAI(userId)
                _aiFeedback.value = response.body()
                Log.d("logovanje", response.body().toString())
            } catch (e: Exception) {
                Log.d("logovanjeGreška", e.toString())
            }
        }
    }

    fun clearAiFeedback() {
        _aiFeedback.value = null
    }

    fun analyzeImprovements(userId: Int) {
        viewModelScope.launch {
            _improvementsLoading.value = true
            _improvementsError.value = null
            _improvements.value = null
            try {
                val response = NetworkModule.apiService.getResumeImprovements(userId)
                if (response.isSuccessful) {
                    _improvements.value = response.body()
                } else {
                    val err = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                    _improvementsError.value = err
                    Log.e("UploadViewModel", "Improvements error: $err")
                }
            } catch (e: Exception) {
                _improvementsError.value = e.message ?: "Neuspjeh pri dohvaćanju prijedloga"
                Log.e("UploadViewModel", "Improvements exception", e)
            } finally {
                _improvementsLoading.value = false
            }
        }
    }

    fun clearImprovements() {
        _improvements.value = null
        _improvementsError.value = null
        _improvementsLoading.value = false
    }
}
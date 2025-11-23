package com.example.myapplication

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

class UploadViewModel : ViewModel() {
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun uploadResume(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            try {
                val part = withContext(Dispatchers.IO) {
                    uriToMultipart(context, uri, "file")
                }
                val response = NetworkModule.apiService.uploadResume(part)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uploadState.value = UploadState.Success(body.message ?: "Uspješno učitano")
                    } else {
                        _uploadState.value = UploadState.Success("Uspjeh (bez tijela odgovora)")
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

    fun reset() {
        _uploadState.value = UploadState.Idle
    }
}

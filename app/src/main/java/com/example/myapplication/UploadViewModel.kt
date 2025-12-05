package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

class UploadViewModel : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val apiService = NetworkModule.apiService

    private fun getAuthToken(): String {
        // This needs to be replaced with your actual token retrieval logic
        return "Bearer faketoken123"
    }

    fun uploadResume(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            val filePart = uriToMultipart(context, uri, "file")
            if (filePart == null) {
                _uploadState.value = UploadState.Error("Could not read the selected file.")
                return@launch
            }
            try {
                val response = apiService.uploadCv(getAuthToken(), filePart)
                if (response.isSuccessful) {
                    _uploadState.value = UploadState.Success("CV uploaded successfully.")
                } else {
                    val errorMsg = "Error: ${response.code()}"
                    _uploadState.value = UploadState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Network error: ${e.message}")
            }
        }
    }

    fun updateResume(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            val filePart = uriToMultipart(context, uri, "file")
            if (filePart == null) {
                _uploadState.value = UploadState.Error("Could not read the selected file.")
                return@launch
            }
            try {
                val response = apiService.updateCv(getAuthToken(), filePart)
                if (response.isSuccessful) {
                    _uploadState.value = UploadState.Success("CV updated successfully.")
                } else {
                     val errorMsg = when (response.code()) {
                        404 -> "Operation failed: CV not found."
                        401, 403 -> "You are not authorized for this action."
                        else -> "Server error: ${response.code()}"
                    }
                    _uploadState.value = UploadState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Network error: ${e.message}")
            }
        }
    }

    fun deleteResume() {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            try {
                val response = apiService.deleteCv(getAuthToken())
                if (response.isSuccessful) {
                    _uploadState.value = UploadState.Success("CV deleted successfully!")
                } else {
                    val errorMsg = when (response.code()) {
                        404 -> "CV not found and cannot be deleted."
                        401, 403 -> "You are not authorized for this action."
                        else -> "Server error: ${response.code()}"
                    }
                    _uploadState.value = UploadState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Network error: ${e.message}")
            }
        }
    }

    fun reset() {
        _uploadState.value = UploadState.Idle
    }

    private fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        var fileName: String? = null
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestBody = bytes.toRequestBody(
                contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "application/octet-stream".toMediaTypeOrNull()
            )

            return MultipartBody.Part.createFormData(partName, fileName ?: "unknown_file", requestBody)
        } catch (e: Exception) {
            Log.e("UploadViewModel", "Failed to convert URI to MultipartBody.Part", e)
            return null
        }
    }
}
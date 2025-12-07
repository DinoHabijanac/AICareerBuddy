package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

class UploadViewModel : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    private val _fileId = MutableStateFlow<Int?>(null)
    val fileId: StateFlow<Int?> = _fileId.asStateFlow()

    private val _fileGuid = MutableStateFlow<String?>(null)
    val fileGuid: StateFlow<String?> = _fileGuid.asStateFlow()

    private val _fileName = MutableStateFlow<String?>(null)
    val fileName: StateFlow<String?> = _fileName.asStateFlow()

    private lateinit var prefs: SharedPreferences
    private val apiService = NetworkModule.apiService

    fun initialize(preferences: SharedPreferences) {
        prefs = preferences
        val savedId = prefs.getInt("resume_id", -1).takeIf { it != -1 }
        val savedGuid = prefs.getString("resume_guid", null)
        val savedFileName = prefs.getString("resume_filename", null)

        Log.d("UploadViewModel", "Initializing - ID: $savedId, GUID: $savedGuid, FileName: $savedFileName")

        _fileId.value = savedId
        _fileGuid.value = savedGuid
        _fileName.value = savedFileName
    }

    fun clearLocalState() {
        _fileId.value = null
        _fileGuid.value = null
        _fileName.value = null
        prefs.edit {
            remove("resume_id")
            remove("resume_guid")
            remove("resume_filename")
            apply()
        }
        Log.d("UploadViewModel", "Local state cleared")
    }

    private fun getAuthToken(): String {
        return "Bearer faketoken123"
    }

    private fun getUserIdRequestBody(): RequestBody {
        val userId = "1"
        return userId.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun setTemporaryState(state: UploadState, duration: Long = 3000) {
        viewModelScope.launch {
            _uploadState.value = state
            delay(duration)
            if (_uploadState.value == state) {
                _uploadState.value = UploadState.Idle
            }
        }
    }

    fun uploadOrUpdateResume(context: Context, uri: Uri) {
        val isUpdate = _fileId.value != null
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading

            Log.d("UploadViewModel", "Starting upload/update - isUpdate: $isUpdate")

            val filePart = uriToMultipart(context, uri, "file")
            if (filePart == null) {
                setTemporaryState(UploadState.Error("Could not read the selected file."))
                return@launch
            }

            try {
                val response = if (isUpdate) {
                    Log.d("UploadViewModel", "Calling updateCv")
                    apiService.updateCv(getAuthToken(), filePart)
                } else {
                    Log.d("UploadViewModel", "Calling uploadCv")
                    val userIdBody = getUserIdRequestBody()
                    apiService.uploadCv(getAuthToken(), filePart, userIdBody)
                }

                Log.d("UploadViewModel", "Response - Success: ${response.isSuccessful}, Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val cvInfo = response.body()!!

                    Log.d("UploadViewModel", "RAW Response body: $cvInfo")
                    Log.d("UploadViewModel", "Server returned - id: ${cvInfo.id}, name: ${cvInfo.name}")
                    Log.d("UploadViewModel", "Extracted - GUID: ${cvInfo.fileGuid}, FileName: ${cvInfo.fileName}")

                    _fileId.value = cvInfo.id
                    _fileGuid.value = cvInfo.fileGuid
                    _fileName.value = cvInfo.fileName

                    Log.d("UploadViewModel", "StateFlow updated - ID: ${_fileId.value}, GUID: ${_fileGuid.value}, FileName: ${_fileName.value}")

                    prefs.edit {
                        putInt("resume_id", cvInfo.id)
                        putString("resume_guid", cvInfo.fileGuid)
                        putString("resume_filename", cvInfo.fileName)
                        apply()
                    }

                    Log.d("UploadViewModel", "Saved to SharedPreferences")

                    val message = if (isUpdate) "CV updated successfully." else "CV uploaded successfully."
                    setTemporaryState(UploadState.Success(message))

                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.errorBody()?.string()}"
                    Log.e("UploadViewModel", "Upload/Update failed: $errorMsg")
                    setTemporaryState(UploadState.Error(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Upload/Update exception", e)
                setTemporaryState(UploadState.Error("Network error: ${e.message}"))
            }
        }
    }

    fun deleteResume() {
        val id = _fileId.value ?: return

        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            Log.d("UploadViewModel", "Deleting CV - ID: $id")
            Log.d("UploadViewModel", "Delete URL will be: api/Resume/$id")

            try {
                val response = apiService.deleteCv(getAuthToken(), id)
                Log.d("UploadViewModel", "Delete response - Success: ${response.isSuccessful}, Code: ${response.code()}")

                if (response.isSuccessful) {
                    _fileId.value = null
                    _fileGuid.value = null
                    _fileName.value = null

                    Log.d("UploadViewModel", "StateFlow cleared")

                    prefs.edit {
                        remove("resume_id")
                        remove("resume_guid")
                        remove("resume_filename")
                        apply()
                    }

                    setTemporaryState(UploadState.Success("CV deleted successfully!"))

                } else if (response.code() == 404) {
                    Log.w("UploadViewModel", "File not found on server (404), clearing local state")
                    _fileId.value = null
                    _fileGuid.value = null
                    _fileName.value = null
                    prefs.edit {
                        remove("resume_id")
                        remove("resume_guid")
                        remove("resume_filename")
                        apply()
                    }
                    setTemporaryState(UploadState.Success("CV was already deleted."))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = "Delete failed: ${response.code()} - $errorBody"
                    Log.e("UploadViewModel", errorMsg)
                    setTemporaryState(UploadState.Error(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Delete exception", e)
                setTemporaryState(UploadState.Error("Network error: ${e.message}"))
            }
        }
    }

    private fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        var fileName: String? = null
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                }
            }

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestBody = bytes.toRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, fileName ?: "unknown_file", requestBody)
        } catch (e: Exception) {
            Log.e("UploadViewModel", "Failed to convert URI to MultipartBody.Part", e)
            return null
        }
    }
}

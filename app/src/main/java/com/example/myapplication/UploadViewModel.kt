package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
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
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.abs

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

    private val _fileName = MutableStateFlow<String?>(null)
    val fileName: StateFlow<String?> = _fileName.asStateFlow()

    private val _filePath = MutableStateFlow<String?>(null)
    val filePath: StateFlow<String?> = _filePath.asStateFlow()

    private lateinit var prefs: SharedPreferences
    private val apiService = NetworkModule.apiService
    private var deviceUserId: Int = 0

    fun initialize(context: Context, preferences: SharedPreferences) {
        prefs = preferences

        // Get or create device-based user ID
        deviceUserId = getDeviceUserId(context)
        Log.d("UploadViewModel", "Device User ID: $deviceUserId")

        // Load from local storage first
        val savedId = prefs.getInt("resume_id", -1).takeIf { it != -1 }
        val savedFileName = prefs.getString("resume_filename", null)
        val savedFilePath = prefs.getString("resume_path", null)

        Log.d("UploadViewModel", "Local storage - ID: $savedId, FileName: $savedFileName")

        _fileId.value = savedId
        _fileName.value = savedFileName
        _filePath.value = savedFilePath

        // Fetch from server to sync state
        fetchResumeFromServer()
    }

    /**
     * Gets a unique user ID based on the device's Android ID.
     * This ensures each device has a consistent user ID until login is implemented.
     */
    private fun getDeviceUserId(context: Context): Int {
        val storedUserId = prefs.getInt("device_user_id", -1)

        if (storedUserId != -1) {
            return storedUserId
        }

        // Generate from Android ID
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val userId = abs(androidId.hashCode())

        // Save for future use
        prefs.edit {
            putInt("device_user_id", userId)
            apply()
        }

        Log.d("UploadViewModel", "Generated new device user ID: $userId from Android ID: $androidId")
        return userId
    }

    /**
     * Fetches the user's resume from the server to sync local state.
     */
    private fun fetchResumeFromServer() {
        viewModelScope.launch {
            try {
                Log.d("UploadViewModel", "Fetching resume from server for user: $deviceUserId")

                val response = apiService.getResumeByUserId(getAuthToken(), deviceUserId)

                if (response.isSuccessful && response.body() != null) {
                    val cvInfo = response.body()!!

                    Log.d("UploadViewModel", "Server has resume - ID: ${cvInfo.id}, Name: ${cvInfo.name}")

                    // Update state with server data
                    _fileId.value = cvInfo.id
                    _fileName.value = cvInfo.name ?: "Životopis${cvInfo.extension}"
                    _filePath.value = cvInfo.path

                    // Save to SharedPreferences
                    prefs.edit {
                        putInt("resume_id", cvInfo.id)
                        putString("resume_filename", cvInfo.name ?: "Životopis${cvInfo.extension}")
                        putString("resume_path", cvInfo.path)
                        apply()
                    }

                    Log.d("UploadViewModel", "State synced with server")

                } else if (response.code() == 404) {
                    // No resume on server, clear local state if any
                    Log.d("UploadViewModel", "No resume found on server (404)")
                    if (_fileId.value != null) {
                        Log.d("UploadViewModel", "Clearing stale local state")
                        clearLocalState()
                    }
                } else {
                    Log.w("UploadViewModel", "Failed to fetch resume: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error fetching resume from server", e)
                // Keep local state if network fails
            }
        }
    }

    fun clearLocalState() {
        _fileId.value = null
        _fileName.value = null
        _filePath.value = null
        prefs.edit {
            remove("resume_id")
            remove("resume_filename")
            remove("resume_path")
            apply()
        }
        Log.d("UploadViewModel", "Local state cleared")
    }

    private fun getAuthToken(): String {
        return "Bearer faketoken123"
    }

    private fun getUserIdRequestBody(): okhttp3.RequestBody {
        val userId = deviceUserId.toString()
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

    /**
     * Uploads a new resume or updates an existing one.
     */
    fun uploadOrUpdateResume(context: Context, uri: Uri) {
        val currentFileId = _fileId.value
        val isUpdate = currentFileId != null

        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading

            Log.d("UploadViewModel", "Starting ${if (isUpdate) "update" else "upload"} - ID: $currentFileId, UserID: $deviceUserId")

            // Get original filename
            val originalFileName = getFileNameFromUri(context, uri)
            Log.d("UploadViewModel", "Original filename: $originalFileName")

            val filePart = uriToMultipart(context, uri, "file")
            if (filePart == null) {
                setTemporaryState(UploadState.Error("Nije moguće pročitati odabranu datoteku."))
                return@launch
            }

            try {
                val response = if (isUpdate) {
                    Log.d("UploadViewModel", "Calling updateCv with ID: $currentFileId")
                    val userIdBody = getUserIdRequestBody()
                    apiService.updateCv(getAuthToken(), currentFileId, filePart, userIdBody)
                } else {
                    Log.d("UploadViewModel", "Calling uploadCv")
                    val userIdBody = getUserIdRequestBody()
                    apiService.uploadCv(getAuthToken(), filePart, userIdBody)
                }

                Log.d("UploadViewModel", "Response - Success: ${response.isSuccessful}, Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val cvInfo = response.body()!!

                    Log.d("UploadViewModel", "RAW Response: $cvInfo")
                    Log.d("UploadViewModel", "Server returned - id: ${cvInfo.id}, name: ${cvInfo.name}")

                    // Validate response
                    if (cvInfo.id == 0 || cvInfo.name.isNullOrBlank()) {
                        Log.e("UploadViewModel", "Invalid response data")
                        setTemporaryState(UploadState.Error("Greška: poslužitelj vratio neispravne podatke"))
                        return@launch
                    }

                    // Use original filename for display, not the GUID
                    val displayName = originalFileName ?: cvInfo.name

                    // Update state
                    _fileId.value = cvInfo.id
                    _fileName.value = displayName
                    _filePath.value = cvInfo.path

                    Log.d("UploadViewModel", "StateFlow updated - ID: ${_fileId.value}, FileName: ${_fileName.value}")

                    // Save to SharedPreferences
                    prefs.edit {
                        putInt("resume_id", cvInfo.id)
                        putString("resume_filename", displayName)
                        putString("resume_path", cvInfo.path)
                        apply()
                    }

                    Log.d("UploadViewModel", "Saved to SharedPreferences")

                    val message = if (isUpdate) "Životopis je ažuriran" else "Životopis je uspješno učitan"
                    setTemporaryState(UploadState.Success(message))

                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    val errorMsg = when (response.code()) {
                        400 -> "Neispravna datoteka. Provjerite format i veličinu."
                        409 -> {
                            // User already has a resume but we didn't know
                            Log.w("UploadViewModel", "Got 409 - fetching existing resume")
                            fetchResumeFromServer()
                            "Već imate učitan životopis. Koristite 'Uredi' za zamjenu."
                        }
                        404 -> "Životopis nije pronađen."
                        else -> "Greška: ${response.code()}"
                    }
                    Log.e("UploadViewModel", "Upload/Update failed: $errorMsg - $errorBody")
                    setTemporaryState(UploadState.Error(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Upload/Update exception", e)
                setTemporaryState(UploadState.Error("Greška mreže: ${e.message}"))
            }
        }
    }

    /**
     * Deletes the current user's resume
     */
    fun deleteResume() {
        val id = _fileId.value
        if (id == null) {
            Log.w("UploadViewModel", "Delete called but no file ID found")
            return
        }

        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            Log.d("UploadViewModel", "Deleting CV - ID: $id, UserID: $deviceUserId")

            try {
                val response = apiService.deleteCv(getAuthToken(), id, deviceUserId)
                Log.d("UploadViewModel", "Delete response - Success: ${response.isSuccessful}, Code: ${response.code()}")

                if (response.isSuccessful) {
                    // Clear state
                    clearLocalState()
                    setTemporaryState(UploadState.Success("Životopis je izbrisan"))
                    Log.d("UploadViewModel", "CV deleted successfully")

                } else if (response.code() == 404) {
                    Log.w("UploadViewModel", "File not found on server (404), clearing local state")
                    clearLocalState()
                    setTemporaryState(UploadState.Success("Životopis je već bio izbrisan"))

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = when (response.code()) {
                        403 -> "Nemate dozvolu za brisanje ovog životopisa"
                        else -> "Greška pri brisanju: ${response.code()}"
                    }
                    Log.e("UploadViewModel", "$errorMsg - $errorBody")
                    setTemporaryState(UploadState.Error(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Delete exception", e)
                setTemporaryState(UploadState.Error("Greška mreže: ${e.message}"))
            }
        }
    }

    /**
     * Gets the original filename from a URI
     */
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            Log.e("UploadViewModel", "Error getting filename from URI", e)
        }
        return fileName
    }

    /**
     * Converts a URI to a MultipartBody.Part for file upload
     */
    private fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(context, uri)

        try {
            // Read file content
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            // Create multipart body
            val requestBody = bytes.toRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, fileName ?: "unknown_file", requestBody)

        } catch (e: Exception) {
            Log.e("UploadViewModel", "Failed to convert URI to MultipartBody.Part", e)
            return null
        }
    }
}
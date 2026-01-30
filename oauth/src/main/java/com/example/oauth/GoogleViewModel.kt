package com.example.oauth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.models.LoginRequest
import com.example.core.models.RegistrationRequest
import com.example.core.models.LoginResponse
import com.example.core.models.RegistrationResponse
import com.example.core.network.NetworkModule
import kotlinx.coroutines.launch
import retrofit2.Response

class GoogleLoginViewModel : ViewModel() {
    val status: MutableLiveData<String> = MutableLiveData("")
    val statusReg: MutableLiveData<String> = MutableLiveData("")
    val userId: MutableLiveData<Int> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val errorMessage: MutableLiveData<String> = MutableLiveData("")

    fun loginUserWithGoogle(loginRequest: LoginRequest, onComplete: (Boolean) -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> = NetworkModule.apiService.loginUserWithGoogle(request = loginRequest)
                isLoading.postValue(false)

                val body = response.body()
                status.postValue(response.code().toString())
                userId.postValue(body?.user?.id)
                username.postValue(body?.user?.username)

                val success = response.isSuccessful && body?.success == true
                onComplete(success)
            } catch (e: Exception) {
                isLoading.postValue(false)
                Log.e("GoogleLoginVM", "Greška: ${e.message}", e)
                status.postValue("Greška pri prijavi sa google-om ${e.message}")
                onComplete(false)
            }
        }
    }

    fun registerGoogle(request: RegistrationRequest, onComplete: (Boolean) -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<RegistrationResponse> = NetworkModule.apiService.registerUserWithGoogle(request)
                isLoading.postValue(false)

                val body = response.body()
                statusReg.postValue(response.code().toString())
                userId.postValue(body?.userId ?: 1)
                username.postValue(body?.username ?: "")

                val success = response.isSuccessful
                onComplete(success)
            } catch (e: Exception) {
                isLoading.postValue(false)
                Log.e("GoogleLoginVM", "Greška: ${e.message}", e)
                statusReg.postValue("Greška pri prijavi sa google-om ${e.message}")
                onComplete(false)
            }
        }
    }
}
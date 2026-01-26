package com.example.oauth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.models.LoginRequest
import com.example.core.models.RegistrationRequest
import com.example.core.network.NetworkModule
import kotlinx.coroutines.launch

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
                val response = NetworkModule.apiService.loginUserWithGoogle(request = loginRequest)
                isLoading.postValue(false)

                status.postValue(response.code().toString())
                userId.postValue(response.body()?.user?.id)
                username.postValue(response.body()?.user?.username)

                val success = response.isSuccessful && response.body()?.success == true
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
                val response = NetworkModule.apiService.registerUserWithGoogle(request)
                isLoading.postValue(false)

                statusReg.postValue(response.code().toString())
                userId.postValue(response.body()?.userId ?: 1)
                username.postValue(response.body()?.username ?: "")

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
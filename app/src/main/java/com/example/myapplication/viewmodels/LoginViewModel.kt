package com.example.myapplication.viewmodels

import android.util.Log
import androidx.compose.ui.res.stringArrayResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.models.LoginRequest
import com.example.core.models.LoginResponse
import com.example.core.network.NetworkModule
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val status: MutableLiveData<String> = MutableLiveData("")
    val userId: MutableLiveData<Int> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    private val _errorMessage = MutableLiveData("")
    val errorMessage = _errorMessage

    val isLoading = MutableLiveData(false)

    fun loginUser(onSuccess: (Int, String) -> Unit, onFail: () -> Unit) {
        val u = (username.value ?: "").trim()
        val p = password.value ?: ""

        if (u.isBlank() || p.isBlank()) {
            _errorMessage.value = "Unesite korisničko ime i lozinku"
            onFail()
            return
        }

        isLoading.value = true
        _errorMessage.value = ""

        val req = LoginRequest(u, p)

        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.loginUser(req)
                isLoading.value = false

                if (!response.isSuccessful) {
                    _errorMessage.value = response.errorBody()?.string() ?: "Greška ${response.code()}"
                    onFail()
                    return@launch
                }

                val body: LoginResponse? = response.body()
                val user = body?.user

                if (body?.success == true && user != null) {
                    // UZMI PRAVI ID IZ body.user.id
                    onSuccess(user.id, user.username)
                } else {
                    _errorMessage.value = body?.message ?: "Neispravan odgovor servera"
                    onFail()
                }
            } catch (e: Exception) {
                isLoading.value = false
                _errorMessage.value = "Greška pri povezivanju: ${e.message}"
                onFail()
            }
        }
    }

    fun loginUserWithGoogle(loginRequest: LoginRequest){
        //ZAVRŠITI
        isLoading.value = true
        viewModelScope.launch {
            try {
                //TODO IMPLEMENTIRATI HASHIRANJE OVDJE
                val response = NetworkModule.apiService.loginUserWithGoogle(request = loginRequest)
                isLoading.value = false
                status.postValue(response.code().toString())
                userId.postValue(response.body()?.user?.id)
                username.postValue(response.body()?.user?.username)
            }
            catch (e : Exception){
                status.postValue("Greška pri prijavi sa google-om ${e.message}")
            }
        }
    }
}
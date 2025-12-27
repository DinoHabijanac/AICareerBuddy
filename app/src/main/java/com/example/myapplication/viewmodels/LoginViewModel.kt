package com.example.myapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.LoginRequest
import com.example.myapplication.network.LoginResponse
import com.example.myapplication.network.NetworkModule
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

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
}
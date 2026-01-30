package com.example.myapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.NetworkModule
import com.example.core.models.RegistrationRequest
import com.example.core.models.RegistrationResponse
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    // Stanja forme
    val firstName: MutableLiveData<String> = MutableLiveData("")
    val lastName: MutableLiveData<String> = MutableLiveData("")
    val username: MutableLiveData<String> = MutableLiveData("")
    val email: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val confirmPassword: MutableLiveData<String> = MutableLiveData("")
    val role: MutableLiveData<String> = MutableLiveData("student")

    val possibleRoles = listOf("poslodavac", "student")

    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage = _errorMessage

    val isLoading = MutableLiveData(false)

    fun registerUser(onSuccess: (Int, String) -> Unit, onFail: () -> Unit) {
        // Validacija (primjer: provjera lozinke i confirm lozinke)
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Lozinka i potvrda lozinke nisu isti!"
            onFail()
            return
        }

        isLoading.value = true
        _errorMessage.value = ""
        val req = RegistrationRequest(
            firstName.value ?: "", lastName.value ?: "", username.value ?: "",
            email.value ?: "", password.value ?: "", role.value ?: "student"
        )

        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.registerUser(req)
                isLoading.value = false
                if (response.isSuccessful) {
                    val body: RegistrationResponse? = response.body()
                    val uid = body?.userId
                    if (uid != null) {
                        onSuccess(uid, req.username)  // predaj userId i korisničko ime
                    } else {
                        onSuccess(-1, req.username)
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "Greška ${response.code()}"
                    _errorMessage.value = err
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
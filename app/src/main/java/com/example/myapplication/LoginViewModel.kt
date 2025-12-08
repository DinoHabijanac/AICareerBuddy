// app/src/main/java/com/example/myapplication/LoginViewModel.kt
package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.LoginRequest
import com.example.myapplication.network.LoginResponse
import com.example.myapplication.network.NetworkModule
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // Polja za formu prijave
    val username: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    // Poruka o pogrešci
    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage = _errorMessage

    // Status učitavanja (npr. za prikaz progress indikatora ili onemogućavanje gumba)
    val isLoading = MutableLiveData(false)

    /**
     * Pokreće pokušaj prijave korisnika.
     * @param onSuccess callback s (userId, username) ako je prijava uspješna
     * @param onFail callback u slučaju neuspješne prijave (npr. za ostanak na ekranu i prikaz greške)
     */
    fun loginUser(onSuccess: (Int, String) -> Unit, onFail: () -> Unit) {
        // (Opcionalna validacija: provjera da polja nisu prazna - gumb Prijava je ionako onemogućen dok su polja prazna)
        if ((username.value ?: "").isBlank() || (password.value ?: "").isBlank()) {
            _errorMessage.value = "Unesite korisničko ime i lozinku"
            onFail()
            return
        }

        isLoading.value = true
        _errorMessage.value = ""
        val req = LoginRequest(username.value ?: "", password.value ?: "")

        // Pokretanje mrežnog poziva unutar korutine
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.loginUser(req)
                isLoading.value = false
                if (response.isSuccessful) {
                    val body: LoginResponse? = response.body()
                    val uid = body?.userId
                    if (uid != null) {
                        // Prijava uspješna - pozovi callback s dobijenim userId i korisničkim imenom
                        onSuccess(uid, req.username)
                    } else {
                        // Ako iz nekog razloga nema ID-a u odgovoru, svejedno signaliziraj uspjeh (ID = -1)
                        onSuccess(-1, req.username)
                    }
                } else {
                    // Neuspješna prijava - prikupi poruku greške iz odgovora (ili kod)
                    val errMsg = response.errorBody()?.string() ?: "Greška ${response.code()}"
                    _errorMessage.value = errMsg
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

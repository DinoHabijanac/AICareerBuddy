package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.NetworkModule
import com.example.myapplication.network.RegistrationRequest
import com.example.myapplication.network.RegistrationResponse
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    // Stanja forme
    val firstName: MutableLiveData<String> = MutableLiveData("")
    val lastName:  MutableLiveData<String> = MutableLiveData("")
    val username:  MutableLiveData<String> = MutableLiveData("")
    val email:     MutableLiveData<String> = MutableLiveData("")
    val password:  MutableLiveData<String> = MutableLiveData("")
    val confirmPassword: MutableLiveData<String> = MutableLiveData("")
    val role:      MutableLiveData<String> = MutableLiveData("korisnik") // zadani izbor

    // Moguće uloge
    val possibleRoles = listOf("admin", "korisnik")

    // Poruka pogreške
    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage = _errorMessage

    // Status registracije (npr. za loading)
    val isLoading = MutableLiveData(false)

    /**
     * Pokreće registraciju korisnika.
     * @param onSuccess: callback s (userId, username) ako je uspješno
     * @param onFail: callback bez parametara u slučaju greške
     */
    fun registerUser(onSuccess: (Int, String) -> Unit, onFail: () -> Unit) {
        // Validacija (primjer: provjera lozinke i confirm lozinke)
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Lozinka i potvrda lozinke nisu isti!"
            onFail()
            return
        }
        // Ostale validacije polja mogu se dodati ovdje...
        // (npr. prazna polja, format emaila itd. Po potrebi)

        isLoading.value = true
        _errorMessage.value = ""
        val req = RegistrationRequest(
            firstName.value ?: "", lastName.value ?: "", username.value ?: "",
            email.value ?: "", password.value ?: "", role.value ?: "korisnik"
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
                        // Ako backend ne vraća userId, možemo i tako javiti uspjeh:
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

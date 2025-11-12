package hr.foi.air.giveaway.ui.register
import hr.foi.air.giveaway.data.model.RegisterRequest
import hr.foi.air.giveaway.data.model.RegisterResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.giveaway.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Definicija stanja UI-a za registraciju (seald class za MVVM stanje)
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    // StateFlow koji drži trenutno stanje registracijskog procesa
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    // Funkcija za pokretanje registracije korisnika
    fun registerUser(username: String, email: String, password: String, confirmPassword: String) {
        // 1. Lokalna validacija lozinke (provjera podudaranja lozinke i potvrde)
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Lozinke se ne podudaraju")
            return
        }
        // 2. Pokretanje mrežnog poziva u korutini
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                // Kreiranje zahtjeva i slanje na API
                val request = RegisterRequest(username = username, email = email, password = password)
                val response = NetworkModule.apiService.registerUser(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Uspješna registracija - preuzmi poruku iz odgovora ili postavi zadanu
                        _registerState.value = RegisterState.Success(body.message ?: "Registracija uspješna")
                    } else {
                        _registerState.value = RegisterState.Success("Registracija uspješna")
                    }
                } else {
                    // Greška na strani servera (npr. 400/500) - očitaj poruku greške ako postoji
                    val errorMsg = response.errorBody()?.string() ?: "HTTP ${'$'}{response.code()}"
                    _registerState.value = RegisterState.Error(errorMsg)
                }
            } catch (e: Exception) {
                // Greška prilikom poziva (npr. nema veze, iznimka i sl.)
                _registerState.value = RegisterState.Error(e.message ?: "Neuspjeh pri registraciji")
            }
        }
    }

    // Opcionalno: resetiranje stanja nazad na Idle (npr. nakon prikaza poruke ili za ponovni pokušaj)
    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}

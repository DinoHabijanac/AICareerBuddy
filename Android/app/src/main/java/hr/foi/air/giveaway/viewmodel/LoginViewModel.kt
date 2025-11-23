package hr.foi.air.giveaway.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.giveaway.model.LoginRequest
import hr.foi.air.giveaway.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // Uključi/isključi hardkodirani login ovdje
    private val HARD_CODED_LOGIN_ENABLED = false // promijeni u false da vratiš realni poziv

    // Hardkodirani podaci (za demo)
    private val demoUsername = "test"
    private val demoPassword = "1234"
    private val demoSuccessMessage = "Prijava uspješna (hardkodirano)."

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Polja ne smiju biti prazna.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            if (HARD_CODED_LOGIN_ENABLED) {
                // simuliraj mali delay kao mrežni poziv
                delay(600)
                if (username == demoUsername && password == demoPassword) {
                    _loginState.value = LoginState.Success(demoSuccessMessage)
                } else {
                    _loginState.value = LoginState.Error("Neispravni korisnički podaci ")
                }
                return@launch
            }

            // ORIGINALNI (mrežni) kod - izvršit će se samo ako HARD_CODED_LOGIN_ENABLED == false
            try {
                val response = RetrofitInstance.api.login(LoginRequest(username, password))
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    if (body.success) {
                        _loginState.value = LoginState.Success(body.message ?: "Prijava uspješna.")
                    } else {
                        _loginState.value = LoginState.Error(body.message ?: "Neispravni korisnički podaci.")
                    }
                } else {
                    _loginState.value = LoginState.Error("Greška: neuspješan odgovor s poslužitelja.")
                }

            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Greška u komunikaciji s poslužiteljem: ${e.message}")
            }
        }
    }
}

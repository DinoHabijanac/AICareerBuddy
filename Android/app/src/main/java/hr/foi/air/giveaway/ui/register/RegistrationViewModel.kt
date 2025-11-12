package hr.foi.air.giveaway.ui.register
import hr.foi.air.giveaway.data.model.RegisterRequest
import hr.foi.air.giveaway.data.model.RegisterResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.giveaway.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState


    fun registerUser(username: String, email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Lozinke se ne podudaraju")
            return
        }
         viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val request = RegisterRequest(username = username, email = email, password = password)
                val response = NetworkModule.apiService.registerUser(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _registerState.value = RegisterState.Success(body.message ?: "Registracija uspješna")
                    } else {
                        _registerState.value = RegisterState.Success("Registracija uspješna")
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "HTTP ${'$'}{response.code()}"
                    _registerState.value = RegisterState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Neuspjeh pri registraciji")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}

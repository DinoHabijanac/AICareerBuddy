package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ViewCVUiState(
    val isLoading: Boolean = false,
    val cvFile: File? = null,
    val error: String? = null,
)

class ViewCVViewModel : ViewModel(){
    private val _viewState = MutableStateFlow(ViewCVUiState())
    val state = _viewState.asStateFlow()

    private fun loadCv(forceRefresh: Boolean = false){
        viewModelScope.launch {
            _viewState.value = ViewCVUiState(isLoading = true)

            try{
                val file = loadDummyFile() //TODO implementirati file upload

                if(file == null){
                    _viewState.value = ViewCVUiState(
                        error = "Nema uploadanog životopisa. Pokušajte ponovo!"
                    )
                }else{
                    _viewState.value = ViewCVUiState(cvFile = file)
                }
            } catch (e: Exception){
                _viewState.value = ViewCVUiState(error = "Greška kod učitavanja životopisa ${e.message}")
            }
        }
    }
    private fun loadDummyFile(): File? = null //TODO implementirati funkciju za load životopisa
}
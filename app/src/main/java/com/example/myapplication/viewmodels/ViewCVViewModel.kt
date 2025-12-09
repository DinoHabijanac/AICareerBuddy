package com.example.myapplication.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ViewCVUiState(
    val isLoading: Boolean = false,
    val pdfUri: Uri? = null,
    val error: String? = null,
)

class ViewCVViewModel : ViewModel(){
    private val _viewState = MutableStateFlow(ViewCVUiState())
    val viewState: StateFlow<ViewCVUiState> = _viewState

    fun refreshCv(context: Context){
        viewModelScope.launch {
            _viewState.value = ViewCVUiState(isLoading = true)

            try{
                val uri = Uri.parse("file:///assets/cv.pdf") // iskoristiti server value kod implementacije

                context.contentResolver.openInputStream(uri)?.close()

                _viewState.value = ViewCVUiState(
                    isLoading = false,
                    pdfUri = uri
                )

            } catch (e: Exception){
                _viewState.value = ViewCVUiState(error = "Greška kod učitavanja životopisa ${e.message}")
            }
        }
    }
}
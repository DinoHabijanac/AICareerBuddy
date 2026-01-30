package com.example.oauth

import android.content.Context
import androidx.annotation.RequiresApi

interface IGoogleLogin {
    @RequiresApi(34)
    suspend fun signIn(
        context: Context,
        webClientId: String,
        filterByAuthorizedAccounts: Boolean = true
    ): GoogleLoginResult

    @RequiresApi(34)
    suspend fun signInWithFallback(
        context: Context,
        webClientId: String
    ): GoogleLoginResult
}
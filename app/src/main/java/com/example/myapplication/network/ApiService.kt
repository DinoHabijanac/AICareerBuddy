// app/src/main/java/com/example/myapplication/network/ApiService.kt
package com.example.myapplication.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Body

interface ApiService {
    @Headers("Accept: application/json")
    @POST("api/Auth/register")
    suspend fun registerUser(@Body request: RegistrationRequest): Response<RegistrationResponse>

    // **NOVO**: Login korisnika
    @Headers("Accept: application/json")
    @POST("api/Auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}

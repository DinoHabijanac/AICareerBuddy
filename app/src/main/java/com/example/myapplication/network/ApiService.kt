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

data class UploadResponse(
    val success: Boolean = false,
    val message: String? = null,
    val fileUrl: String? = null
)

interface ApiService {
   // @Multipart
    @Headers("Accept: application/json")

    @POST("api/Registration/register")  // TODO: provjeriti ispravan endpoint
    suspend fun registerUser(@Body request: RegistrationRequest): Response<RegistrationResponse>
}


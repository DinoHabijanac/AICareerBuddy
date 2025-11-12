package hr.foi.air.giveaway.network

import hr.foi.air.giveaway.model.LoginRequest
import hr.foi.air.giveaway.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

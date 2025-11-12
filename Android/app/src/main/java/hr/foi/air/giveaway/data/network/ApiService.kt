import hr.foi.air.giveaway.data.model.RegisterRequest
import hr.foi.air.giveaway.data.model.RegisterResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Accept: application/json")
    @POST("api/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>
}

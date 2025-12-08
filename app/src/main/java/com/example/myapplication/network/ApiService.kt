package com.example.myapplication.network

import com.example.myapplication.models.JobListing
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class UploadResponse(
    val success: Boolean = false,
    val message: String? = null,
    val fileUrl: String? = null
)

interface ApiService {

    //RESUME
    @Multipart
    @Headers("Accept: application/json")
    @POST("api/Resume")
    suspend fun uploadResume(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Response<UploadResponse>

    //JOBS

    @GET("api/Job")
    suspend fun getJobs(): List<JobListing>

    @POST(value = "api/Job")
    suspend fun postJob(@Body job: JobListing) : Response<UploadResponse>

}

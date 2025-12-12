// app/src/main/java/com/example/myapplication/network/ApiService.kt
package com.example.myapplication.network

import com.example.myapplication.models.JobApplication
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
import retrofit2.http.Query

data class UploadResponse(
    val success: Boolean = false,
    val message: String? = null,
    val fileUrl: String? = null
)

interface ApiService {

    // ---------- AUTH / REGISTRATION ----------

    @Headers("Accept: application/json")
    @POST("api/Registration/register")
    suspend fun registerUser(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @Headers("Accept: application/json")
    @POST("api/Auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>


    // ---------- RESUME ----------

    @Multipart
    @Headers("Accept: application/json")
    @POST("api/Resume")
    suspend fun uploadResume(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Response<UploadResponse>


    // ---------- JOBS ----------

    @GET("api/Job")
    suspend fun getJobs(): List<JobListing>

    @Headers("Accept: application/json")
    @POST("api/Job")
    suspend fun postJob(@Body job: JobListing): Response<UploadResponse>


    // ---------- JOB APPLICATIONS ----------

    @GET("api/Application/student")
    suspend fun getJobApplicationsForStudent(@Query("studentId") userId: Int): List<JobApplication>

    @GET("api/Application/employer")
    suspend fun getJobApplicationsForEmployer(@Query("employerId") userId: Int): List<JobApplication>

    @GET("api/Application/job")
    suspend fun getJobApplicationsForJob(@Query("jobId") jobId: Int): List<JobApplication>
}

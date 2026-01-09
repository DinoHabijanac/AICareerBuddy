// app/src/main/java/com/example/myapplication/network/ApiService.kt
package com.example.core.network

import com.example.core.models.JobApplication
import com.example.core.models.JobListing
import com.example.core.models.LoginRequest
import com.example.core.models.LoginResponse
import com.example.core.models.RegistrationRequest
import com.example.core.models.RegistrationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Path

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

    @DELETE("api/Resume/{id}")
    suspend fun deleteResume(
        @Path("id") userId: Int
    ): Response<Unit>

    @Multipart
    @PUT("api/Resume/{id}")
    suspend fun updateResume(
        @Path("id") userId: Int,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    // ---------- JOBS ----------

    @GET("api/Job")
    suspend fun getJobs(): List<JobListing>?

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

    @Headers("Accept: application/json")
    @POST("api/Application")
    suspend fun postApplication(@Body application: JobApplication) : Response<UploadResponse>

    @PUT("api/Application/{id}")
    suspend fun putApplication(@Path("id") id: Int?, @Body application: JobApplication) : Response<UploadResponse>

    @GET("api/Application/id")
    suspend fun getApplication(@Query("id") applicationId: Int) : JobApplication

    @DELETE("api/Application/{id}")
    suspend fun deleteApplication(@Path("id") applicationId: Int) : Response<Unit>
}

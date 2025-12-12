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

    //JOB APPLICATIONS
    @GET(value = "/api/Application/student")
    suspend fun getJobApplicationsForStudent(@Query("studentId") userId: Int) : List<JobApplication>

    @GET(value = "/api/Application/employer")
    suspend fun getJobApplicationsForEmployer(@Query("employerId") userId: Int) : List<JobApplication>

    //trebat ce kasnije kada budemo radili klik na oglas pa da vidimo koji su sve studenti prijavili tocno taj oglas
    @GET(value = "/api/Application/job")
    suspend fun getJobApplicationsForJob(@Query("jobId") jobId: Int) : List<JobApplication>

}

package com.example.myapplication.network

import com.example.myapplication.CvInfo
import com.example.myapplication.models.JobListing
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

data class UploadResponse(
    val success: Boolean = false,
    val message: String? = null,
    val fileUrl: String? = null
)

interface ApiService {

    // ========== RESUME ENDPOINTS ==========

    @Multipart
    @Headers("Accept: application/json")
    @POST("api/Resume")
    suspend fun uploadCv(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Response<CvInfo>

    @Multipart
    @PUT("api/Resume/{id}")
    suspend fun updateCv(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Response<CvInfo>

    @DELETE("api/Resume/{id}")
    suspend fun deleteCv(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("userId") userId: Int
    ): Response<Unit>

    @GET("api/Resume/user/{userId}")
    suspend fun getResumeByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<CvInfo>

    @GET("api/Resume/{id}")
    suspend fun getResume(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<CvInfo>

    // ========== JOB ENDPOINTS ==========

    @GET("api/Job")
    suspend fun getJobs(): List<JobListing>

    @POST("api/Job")
    suspend fun postJob(@Body job: JobListing): Response<UploadResponse>

    @PUT("api/Job/{id}")
    suspend fun updateJob(
        @Path("id") id: Int,
        @Body job: JobListing
    ): Response<Boolean>

    @DELETE("api/Job/{id}")
    suspend fun deleteJob(
        @Path("id") id: Int
    ): Response<Unit>
}
package com.example.myapplication

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    /**
     * Uploads a new CV and returns CvInfo object as response.
     * POST /api/Resume
     */
    @Multipart
    @POST("api/Resume")
    suspend fun uploadCv(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Response<CvInfo2>

    /**
     * Updates an existing CV by its database ID.
     * PUT /api/Resume/{id}
     *
     * Backend expects:
     * - file: the new document file
     * - userId: as form data to verify ownership
     */
    @Multipart
    @PUT("api/Resume/{id}")
    suspend fun updateCv(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Response<CvInfo2>

    /**
     * Deletes a CV by its database ID.
     * DELETE /api/Resume/{id}?userId={userId}
     *
     * Backend verifies that the userId matches the resume owner.
     */
    @DELETE("api/Resume/{id}")
    suspend fun deleteCv(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("userId") userId: Int
    ): Response<Unit>

    /**
     * Gets a resume by user ID.
     * GET /api/Resume/user/{userId}
     */
    @GET("api/Resume/user/{userId}")
    suspend fun getResumeByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<CvInfo2>

    /**
     * Gets a specific resume by ID.
     * GET /api/Resume/{id}
     */
    @GET("api/Resume/{id}")
    suspend fun getResume(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<CvInfo2>
}
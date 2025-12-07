package com.example.myapplication

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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
     * Updates an existing CV.
     * POST /api/Resume/cv/update
     */
    @Multipart
    @POST("api/Resume/cv/update")
    suspend fun updateCv(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<CvInfo2>

    /**
     * Deletes a CV by its database ID.
     * DELETE /api/Resume/{id}
     *
     * TEMPORARY ENDPOINT FOR TESTING - This will be replaced with authenticated
     * endpoint once login/register is implemented by your colleagues.
     */
    @DELETE("api/Resume/{id}")
    suspend fun deleteCv(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    /**
     * Gets a specific resume by ID (for verification purposes).
     * GET /api/Resume/GetResume/{id}
     */
    @GET("api/Resume/GetResume/{id}")
    suspend fun getResume(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<CvInfo2>
}

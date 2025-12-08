package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("userId")   val userId: Int?    = null, // TODO: provjeriti točan naziv polja
    @SerializedName("username") val username: String? = null
)

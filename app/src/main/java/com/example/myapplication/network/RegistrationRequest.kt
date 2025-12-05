package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

// TODO: Polja imenujmo snake_case prema API specifikaciji
data class RegistrationRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name")  val lastName: String,
    @SerializedName("username")   val username: String,
    @SerializedName("email")      val email: String,
    @SerializedName("password")   val password: String,
    @SerializedName("role")       val role: String
)

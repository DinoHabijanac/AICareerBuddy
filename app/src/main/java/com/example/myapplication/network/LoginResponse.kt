// app/src/main/java/com/example/myapplication/network/LoginResponse.kt
package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("username") val username: String? = null
)

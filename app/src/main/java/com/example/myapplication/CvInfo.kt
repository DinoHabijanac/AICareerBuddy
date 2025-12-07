package com.example.myapplication

import com.google.gson.annotations.SerializedName

/**
 * Represents the server's response after a successful CV upload.
 *
 * The server returns:
 * - "id": the database ID of the file record
 * - "name": the actual filename stored on the server (GUID-based)
 * - "path": the full URL to access the file
 * - "size": file size in bytes
 * - "extension": file extension
 * - "createDate": upload date
 * - "userId": the user who uploaded it
 */
data class CvInfo2(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("size")
    val size: Long,

    @SerializedName("extension")
    val extension: String,

    @SerializedName("createDate")
    val createDate: String,

    @SerializedName("userId")
    val userId: Int
) {
    // Helper properties to match your existing code
    val fileGuid: String
        get() = name.removeSuffix(extension) // Extract GUID from "2fa36376-a35a-484a-b1bf-1e39698adc45.pdf"

    val fileName: String
        get() = name // The full filename with extension
}
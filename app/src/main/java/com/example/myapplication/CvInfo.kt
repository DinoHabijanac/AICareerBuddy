package com.example.myapplication

import com.google.gson.annotations.SerializedName

/**
 * Represents the server's response after CV upload/update/get operations.
 *
 * The server returns:
 * - "id": the database ID of the file record (used for update/delete)
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
    val size: Long?,

    @SerializedName("extension")
    val extension: String,

    @SerializedName("createDate")
    val createDate: String?,

    @SerializedName("userId")
    val userId: Int
) {
    /**
     * Returns a user-friendly display name.
     * For GUID-based names like "2fa36376-a35a-484a-b1bf-1e39698adc45.pdf",
     * we show a generic name. You can customize this based on your needs.
     */
    val displayName: String
        get() = if (name.matches(Regex("[0-9a-f-]+\\.[a-z]+", RegexOption.IGNORE_CASE))) {
            // It's a GUID-based filename, show generic name
            "Životopis$extension"
        } else {
            // Use actual filename
            name
        }
}
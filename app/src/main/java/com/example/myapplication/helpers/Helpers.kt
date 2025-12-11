package com.example.myapplication.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import com.example.myapplication.models.JobListing
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun uriToMultipart(
    context: Context,
    uri: Uri,
    partName: String = "file"
): MultipartBody.Part {
    val contentResolver: ContentResolver = context.contentResolver

    val name = queryFileName(contentResolver, uri) ?: "resume"

    val mime = contentResolver.getType(uri) ?: "application/octet-stream"
    val mediaType = mime.toMediaTypeOrNull()

    val input = contentResolver.openInputStream(uri) ?: throw IllegalStateException("Cannot open stream")
    val bytes = input.use { it.readBytes() }

    val requestBody: RequestBody = bytes.toRequestBody(mediaType)
    return MultipartBody.Part.createFormData(partName, name, requestBody)
}

fun queryFileName(contentResolver: ContentResolver, uri: Uri): String? {
    var displayName: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0) displayName = it.getString(idx)
        }
    }
    return displayName
}

public val localDateTimeDeserializer = JsonDeserializer<LocalDateTime> { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
    val str = try { json.asString } catch (_: Exception) { "" }
    if (str.isBlank()) return@JsonDeserializer LocalDateTime.now()
    try {
        val instant = Instant.parse(str)
        LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    } catch (_: Exception) {
        try {
            LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
        } catch (_: Exception) {
            LocalDateTime.now()
        }
    }
}
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)

public val jobListingDeserializer = JsonDeserializer<JobListing> { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
    try {
        val obj = json.asJsonObject
        val id = if (obj.has("id") && !obj.get("id").isJsonNull) obj.get("id").asInt else 0
        val name = if (obj.has("name") && !obj.get("name").isJsonNull) obj.get("name").asString else ""
        val description = if (obj.has("description") && !obj.get("description").isJsonNull) obj.get("description").asString else ""
        val category = if (obj.has("category") && !obj.get("category").isJsonNull) obj.get("category").asString else ""
        val location = if (obj.has("location") && !obj.get("location").isJsonNull) obj.get("location").asString else ""
        val terms = if (obj.has("terms") && !obj.get("terms").isJsonNull) obj.get("terms").asString else ""

        var listingExpiresLdt = LocalDate.now()
        if (obj.has("listingExpires") && !obj.get("listingExpires").isJsonNull) {
            val le = obj.get("listingExpires")
            try {
                val str = if (le.isJsonPrimitive) le.asString else le.toString()
                val instant = Instant.parse(str)
                listingExpiresLdt = LocalDate.ofInstant(instant, ZoneId.systemDefault())
            } catch (_: Exception) {
                try {
                    listingExpiresLdt = LocalDate.parse(le.asString, DateTimeFormatter.ISO_DATE_TIME)
                } catch (_: Exception) {
                    listingExpiresLdt = LocalDate.now()
                }
            }
        }

        val payPerHour = if (obj.has("payPerHour") && !obj.get("payPerHour").isJsonNull) obj.get("payPerHour").asInt else 0
        val employerId = if (obj.has("employerId") && !obj.get("employerId").isJsonNull) obj.get("employerId").asInt else 1

        JobListing(
            employerId = 1,
            name = name,
            description = description,
            category = category,
            location = location,
            listingExpires = listingExpiresLdt,
            terms = terms,
            payPerHour = payPerHour
        )
        //TODO("ispravi na prijavljenog korisnika nakon što se implementira prijava")
    } catch (e: Exception) {
        throw e
    }
}
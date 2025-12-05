package com.example.myapplication.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

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

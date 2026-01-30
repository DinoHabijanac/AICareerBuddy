package com.example.myapplication.views

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun getLoggedUserId() : Int {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userId = prefs.getInt("userId", 0)
    return userId
}

@Composable
fun getJobId() : Int {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("job_prefs", Context.MODE_PRIVATE)
    val jobId = prefs.getInt("jobId", 0)
    return jobId
}

fun formatLocalDateTime(ldt: LocalDate?): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ldt?.format(formatter) ?: LocalDate.now().toString()
    } catch (_: Exception) {
        ldt.toString()
    }
}
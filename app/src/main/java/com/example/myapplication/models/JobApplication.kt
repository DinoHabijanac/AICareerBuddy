package com.example.myapplication.models

data class JobApplication(
    val id: Int,
    val studentId: Int,
    val jobId: Int,
    val dateOfSubmission: java.time.LocalDate?,
    val status: String,
    val expectedPay: Int?,
    val workExperience: String
)

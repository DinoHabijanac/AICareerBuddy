package com.example.myapplication.models

import java.time.LocalDate

data class JobApplication(
    val id: Int,
    val studentId: Int,
    val jobId: Int,
    val dateOfSubmission: String,//LocalDate?,
    val status: String,
    val expectedPay: Int?,
    val workExperience: String
)

package com.example.core.models

data class JobApplication(
    val id : Int?,
    val studentId: Int,
    val jobId: Int,
    val employerId : Int,
    val dateOfSubmission: String,//LocalDate?,
    val status: String,
    val expectedPay: Int?,
    val workExperience: String,
    val education : String,
    val interviewDate : String?
)

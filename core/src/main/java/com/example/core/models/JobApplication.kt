package com.example.core.models

data class JobApplication(
    val id : Int?,
    val studentId: Int,
    val jobId: Int,
    var employerId : Int?,
    val dateOfSubmission: String,//LocalDate?,
    var status: String,
    val expectedPay: Int?,
    val workExperience: String,
    val education : String,
    var interviewDate : String?
)

package com.example.core.models

import com.google.gson.annotations.SerializedName

data class JobApplication(
    val id: Int?,
    val studentId: Int,
    val jobId: Int,
    var employerId: Int?,
    val dateOfSubmission: String,//LocalDate?,
    var status: String,
    val expectedPay: Int?,
    val workExperience: String,
    val education: String,
    var interviewDate: String?,

    @SerializedName("studentName") val studentName: String? = null,
    @SerializedName("jobName") val jobName: String? = null
)

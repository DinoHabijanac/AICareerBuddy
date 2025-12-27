package com.example.core.models
import java.time.LocalDate

data class JobListing(
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val listingExpires: LocalDate,
    val terms: String,
    val payPerHour: Int,
    val employerId: Int
)
package com.example.myapplication.models
import java.time.LocalDate

data class JobListing(
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val listingExpires: LocalDate,
    val terms: String,
    val payPerHour: Int,
    val employerId: Int
)
package com.example.myapplication.models
import java.time.LocalDateTime

data class JobListing(
    val id: Int,
    val employerId : Int,
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val listingExpires: LocalDateTime,
    val terms: List<String>,
    val payPerHour: Int
)
package com.example.core.models

import android.R

data class Student(
    val name : String,
    val lastname : String
){ override fun toString(): String  = "$name $lastname" }


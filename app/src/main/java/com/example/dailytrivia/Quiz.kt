package com.example.dailytrivia

data class Quiz(
    val id: Int,            // Unique ID for each quiz
    val title: String,
    val imageResId: Int,     // Resource ID for the background image
    val email: String
)

package com.example.dailytrivia

data class QuizResult(
    val category: String,            // Unique ID for each quiz
    val score: Double,
    val numberOfQuestions: Int,     // Resource ID for the background image
    val type: String
)

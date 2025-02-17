package com.example.dailytrivia

//data class Question(
//    val text: String,
//    val options: List<String>,
//    val correctAnswerIndex: Int,
//    val funFact: String // New property to store a fun fact for each question
//)

data class Question(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

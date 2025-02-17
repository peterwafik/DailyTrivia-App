package com.example.dailytrivia

data class TriviaResponseQuestions(
    val results: List<Question>
)
data class TriviaResponseCategories(
    val trivia_categories: List<Category>
)
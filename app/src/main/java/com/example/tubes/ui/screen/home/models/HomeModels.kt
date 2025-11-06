package com.example.tubes.ui.screen.home.models

data class CategoryUi(val name: String)
data class AuthorUi(val name: String)
data class QuizUi(
    val title: String,
    val author: String,
    val questions: Int = 3
)
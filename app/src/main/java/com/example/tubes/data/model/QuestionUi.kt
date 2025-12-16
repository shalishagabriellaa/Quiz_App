package com.example.tubes.data.model

data class QuestionUi(
    val id: String = "",
    val question: String = "",
    val options: List<String>,
    val correctAnswerIndex: Int = -1,
    val explanation: String = "",
    val imageUrl: String? = null,
    val category: String,
    val userAnswer: Int? = null,
)

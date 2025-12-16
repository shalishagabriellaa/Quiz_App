package com.example.tubes.data.model

data class QuestionUi(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val explanation: String = "",
    val imageUrl: String? = null
)

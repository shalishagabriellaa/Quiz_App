package com.example.tubes.data.model


data class QuestionFirestore(
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Long = 0L,
    val explanation: String = "",
    val imageUrl: String? = null
)

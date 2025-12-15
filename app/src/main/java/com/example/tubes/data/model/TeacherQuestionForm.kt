package com.example.tubes.data.model

data class TeacherQuestionForm(
    val questionText: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val correctAnswerIndex: Int = -1,
    val explanation: String = "",
    val imageUrl: String? = null
)

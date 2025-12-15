package com.example.tubes.data.model

data class TeacherQuestionBank(
    val id: String,                 // questionId
    val quizId: String,
    val quizTitle: String,
    val difficulty: Difficulty,     // dari quiz
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String?,
    val imageUrl: String?,
    val updatedAt: Long
)


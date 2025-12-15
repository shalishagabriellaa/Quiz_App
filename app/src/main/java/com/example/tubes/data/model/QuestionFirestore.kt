package com.example.tubes.data.model

/**
 * Representasi dokumen Firestore:
 * quizzes/{quizId}/questions/{questionId}
 */
data class QuestionFirestore(
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Long = 0L,
    val explanation: String = "",
    val imageUrl: String? = null
)

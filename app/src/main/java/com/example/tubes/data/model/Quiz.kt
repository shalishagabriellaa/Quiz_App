package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class Quiz(
    val id: String = "",
    val title: String = "",
    val categoryId: String = "",
    val authorId: String = "",
    val questionCount: Long = 0L,
    val bannerUrl: String = "",
    val timer: Long = 0L,                 // waktu total dalam detik
    val quizCode: String = "",           // 6 digit (untuk join)
    val attemptCount: Long = 0L,         // untuk top picks
    val popularity: Long = 0L,           // mirip attemptCount
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class QuestionFirestore(
    val questionId: String = "",
    val quizId: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val answer: String = "",
    val explanation: String = ""
)

data class QuestionUi(
    val id: String,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int, // index 0-3 untuk A-D
    val userAnswer: Int? = null,
    val explanation: String = ""
)

data class QuizUiState(
    val quiz: Quiz? = null,
    val questions: List<QuestionUi> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val timeRemaining: Int = 0,
    val selectedAnswer: Int? = null,
    val userAnswers: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSubmitted: Boolean = false,
    val score: Int = 0,
    val currentUserId: String? = null
)

fun String.toAnswerIndex(): Int {
    return when {
        this.startsWith("A.", ignoreCase = true) -> 0
        this.startsWith("B.", ignoreCase = true) -> 1
        this.startsWith("C.", ignoreCase = true) -> 2
        this.startsWith("D.", ignoreCase = true) -> 3
        else -> 0
    }
}
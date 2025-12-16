package com.example.tubes.data.model

data class QuizUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val quizTitle: String = "",
    val difficulty: String = "",
    val passingGrade: Long = 0L,
    val durationSeconds: Int = 0,

    val questions: List<QuestionUi> = emptyList(),
    val currentQuestionIndex: Int = 0,

    // Map<questionId, selectedOptionIndex>
    val userAnswers: Map<String, Int> = emptyMap(),

    val timeRemaining: Int = 0,
    val isSubmitted: Boolean = false,

    // result
    val correctAnswers: Int = 0,
    val scorePercent: Int = 0,
    val pointsEarned: Int = 0,
    val isPassed: Boolean = false
)

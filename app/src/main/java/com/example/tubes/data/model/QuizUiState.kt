package com.example.tubes.data.model

data class QuizUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val questions: List<QuestionUi> = emptyList(),
    val currentQuestionIndex: Int = 0,

    val timeRemaining: Int = 0,

    // simpan jawaban user sebagai: questionId -> selectedIndex
    val userAnswers: Map<String, Int> = emptyMap(),

    val isSubmitted: Boolean = false
)

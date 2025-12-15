package com.example.tubes.ui.screen

import com.example.tubes.data.model.QuestionUi

data class ExplanationUiState(
    val isLoading: Boolean = true,
    val questions: List<QuestionUi> = emptyList(),
    val userAnswersIndex: Map<String, Int> = emptyMap(), // questionId -> selectedIndex
    val error: String? = null
)

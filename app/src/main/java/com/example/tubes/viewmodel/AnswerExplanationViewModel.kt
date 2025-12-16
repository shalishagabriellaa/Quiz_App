package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.QuestionUi
import com.example.tubes.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnswerExplanationUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val questions: List<QuestionUi> = emptyList(),
    val userAnswersIndex: Map<String, Int> = emptyMap()
)

class AnswerExplanationViewModel(
    private val repo: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnswerExplanationUiState())
    val uiState: StateFlow<AnswerExplanationUiState> = _uiState.asStateFlow()

    fun load(quizId: String, userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AnswerExplanationUiState(isLoading = true)

                val questions = repo.getQuestionsByQuizId(quizId)
                val answers = repo.getLatestAttemptAnswers(userId, quizId)

                _uiState.value = AnswerExplanationUiState(
                    isLoading = false,
                    error = null,
                    questions = questions,
                    userAnswersIndex = answers
                )
            } catch (e: Exception) {
                _uiState.value = AnswerExplanationUiState(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

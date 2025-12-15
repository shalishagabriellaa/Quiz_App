package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.QuizUiState
import com.example.tubes.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class QuizViewModel(
    private val repo: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    private var currentQuizId: String? = null

    fun loadQuiz(quizId: String) {
        currentQuizId = quizId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSubmitted = false,
                currentQuestionIndex = 0,
                userAnswers = emptyMap()
            )

            try {
                val questions = repo.getQuestionsByQuizId(quizId)

                // optional timer (kalau belum mau, biarin 0)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    questions = questions,
                    timeRemaining = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun selectAnswer(selectedIndex: Int) {
        val state = _uiState.value
        if (state.questions.isEmpty()) return
        val q = state.questions[state.currentQuestionIndex]
        _uiState.value = state.copy(
            userAnswers = state.userAnswers + (q.id to selectedIndex)
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        val next = (state.currentQuestionIndex + 1).coerceAtMost(state.questions.lastIndex)
        _uiState.value = state.copy(currentQuestionIndex = next)
    }

    fun previousQuestion() {
        val state = _uiState.value
        val prev = (state.currentQuestionIndex - 1).coerceAtLeast(0)
        _uiState.value = state.copy(currentQuestionIndex = prev)
    }

    fun resetQuiz() {
        _uiState.value = QuizUiState()
    }

    fun submitQuiz(userId: String?) {
        val quizId = currentQuizId ?: return
        if (userId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            try {
                repo.submitQuizResult(
                    userId = userId,
                    quizId = quizId,
                    userAnswers = _uiState.value.userAnswers
                )
                _uiState.value = _uiState.value.copy(isSubmitted = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Submit failed")
            }
        }
    }
}

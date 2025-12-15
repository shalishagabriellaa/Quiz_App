package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.repository.QuizRepository
import com.example.tubes.ui.screen.ExplanationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnswerExplanationViewModel(
    private val repo: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplanationUiState())
    val uiState: StateFlow<ExplanationUiState> = _uiState

    fun load(quizId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val questions = repo.getQuestionsByQuizId(quizId)

                // NOTE:
                // userAnswersIndex belum kita ambil dari DB (karena schema kamu belum simpan per-question).
                // Jadi sementara kosong -> tetap bisa tampil pembahasan + correct answer.
                _uiState.value = ExplanationUiState(
                    isLoading = false,
                    questions = questions,
                    userAnswersIndex = emptyMap(),
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

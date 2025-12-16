package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.TeacherQuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherQuizQrViewModel(
    private val quizRepository: TeacherQuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherQuizQrUiState())
    val uiState: StateFlow<TeacherQuizQrUiState> = _uiState

    fun generateQr(quizId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val expiredAt =
                System.currentTimeMillis() + (5 * 60 * 1000) // 5 menit

            val quizCode =
                quizRepository.generateQuizQr(
                    quizId = quizId,
                    expiredAtMillis = expiredAt
                )

            _uiState.value = TeacherQuizQrUiState(
                quizCode = quizCode,
                expiredAtMillis = expiredAt
            )
        }
    }
}

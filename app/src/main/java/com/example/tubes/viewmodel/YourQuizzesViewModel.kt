package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.HomeRepository
import com.example.tubes.ui.screen.home.models.YourQuizUi
import com.example.tubes.ui.screen.home.models.toYourQuizUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class YourQuizzesUiState(
    val isLoading: Boolean = false,
    val quizzes: List<YourQuizUi> = emptyList(),
    val error: String? = null
)

class YourQuizzesViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(YourQuizzesUiState())
    val uiState: StateFlow<YourQuizzesUiState> = _uiState

    fun loadYourQuizzes(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = repo.getUserQuizResults(userId)
                val ui = results.map { it.toYourQuizUi() }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        quizzes = ui,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}

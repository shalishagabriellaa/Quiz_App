package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.HomeRepository
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CategorySpecifyUiState(
    val isLoading: Boolean = true,
    val quizzes: List<QuizUi> = emptyList(),
    val error: String? = null
)

class CategorySpecifyViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategorySpecifyUiState())
    val uiState: StateFlow<CategorySpecifyUiState> = _uiState

    fun loadQuizzes(categoryId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                // 1. Ambil semua quiz kategori ini
                val quizzes: List<Quiz> = repo.getQuizzesByCategory(categoryId)

                // 2. Cache USER author (supaya dapat name + avatarUrl)
                val authorCache = mutableMapOf<String, User?>()

                quizzes.forEach { quiz ->
                    val authorId = quiz.authorId
                    if (authorId.isNotEmpty() && !authorCache.containsKey(authorId)) {
                        val user = repo.getUser(authorId)
                        authorCache[authorId] = user
                    }
                }

                // 3. Mapping ke QuizUi
                val quizUiList: List<QuizUi> = quizzes.map { quiz ->
                    val author = authorCache[quiz.authorId]
                    val authorName = author?.fullName ?: author?.name ?: "Unknown"
                    val avatarUrl = author?.avatarUrl

                    quiz.toUi(
                        authorName = authorName,
                        authorAvatarUrl = avatarUrl
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    quizzes = quizUiList
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

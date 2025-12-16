package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.HomeRepository
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategorySpecifyUiState(
    val isLoading: Boolean = true,
    val quizzes: List<QuizUi> = emptyList(),
    val categoryBannerUrl: String? = null,
    val error: String? = null
)

class CategorySpecifyViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategorySpecifyUiState())
    val uiState: StateFlow<CategorySpecifyUiState> = _uiState.asStateFlow()

    fun load(categoryId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val quizzes = repo.getQuizzesByCategory(categoryId)
                val category = repo.getCategoryByCategoryId(categoryId)

                // Cache author biar ga nembak berkali-kali
                val authorCache = mutableMapOf<String, User?>()
                quizzes.forEach { q ->
                    val aId = q.authorId
                    if (aId.isNotBlank() && !authorCache.containsKey(aId)) {
                        authorCache[aId] = repo.getUser(aId)
                    }
                }

                // attemptCount per quiz
                val attemptCountMap = mutableMapOf<String, Long>()
                quizzes.forEach { q ->
                    attemptCountMap[q.id] = repo.getAttemptCountByQuizId(q.id)
                }

                val quizUiList = quizzes.map { quiz ->
                    val author = authorCache[quiz.authorId]
                    val authorName =
                        author?.fullName?.takeIf { it.isNotBlank() }
                            ?: author?.email?.substringBefore("@")
                            ?: "Unknown"

                    quiz.toUi(
                        authorName = authorName,
                        authorAvatarUrl = author?.avatarUrl,
                        attemptCount = attemptCountMap[quiz.id] ?: 0L
                    )
                }

                _uiState.value = CategorySpecifyUiState(
                    isLoading = false,
                    quizzes = quizUiList,
                    categoryBannerUrl = category?.bannerUrl,
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

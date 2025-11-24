package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.HomeRepository
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHome(uid: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                Log.d("HomeVM", "Fetching UID: $uid")
                val user = repo.getUser(uid)
                val categories = repo.getCategories()
                val categoriesUi = categories.map { it.toUi() }
                val topAuthors = repo.getTopAuthors()

                // ===== Trending Quizzes =====
                val trending = repo.getTrendingQuizzes()

                val authorCache = mutableMapOf<String, String>()

                trending.forEach { quiz ->
                    val authorId = quiz.authorId
                    if (authorId.isNotEmpty() && !authorCache.containsKey(authorId)) {
                        val authorUser = repo.getUser(authorId)
                        authorCache[authorId] = authorUser?.fullName ?: "Unknown"
                    }
                }

                val trendingUi = trending.map { quiz ->
                    quiz.toUi(authorName = authorCache[quiz.authorId] ?: "Unknown")
                }

                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = null,
                    userName = user?.fullName ?: "Unknown Name",
                    categoriesUi = categoriesUi,
                    trendingUi = trendingUi,
                    topAuthors = topAuthors,
                    avatarUrl = user?.avatarUrl ?: null
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

data class HomeUiState(
    val userName: String = "",
    val avatarUrl: String? = null,
    val categoriesUi: List<CategoryUi> = emptyList(),
    val trendingUi: List<QuizUi> = emptyList(),
    val topAuthors: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)




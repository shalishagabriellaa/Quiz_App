package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizAuthorUi(
    val name: String = "Unknown",
    val avatarUrl: String = ""
)

data class TestInformationUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val quizId: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val participantText: String = "",

    val author: QuizAuthorUi = QuizAuthorUi(),
    val createdTimeMillis: Long = 0L,

    val totalQuestions: Int = 0,
    val durationMinutes: Long = 0L
)

class TestInformationViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestInformationUiState())
    val uiState: StateFlow<TestInformationUiState> = _uiState.asStateFlow()

    fun load(quizId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val quiz = repo.getQuizById(quizId) ?: throw Exception("Quiz not found")

                val user = repo.getUser(quiz.authorId)
                val authorName =
                    user?.fullName?.takeIf { it.isNotBlank() }
                        ?: user?.email?.substringBefore("@")
                        ?: "Unknown"

                val participants = quiz.totalParticipants
                val participantText = "$participants people took this"

                // ✅ durationMinutes di model kamu SUDAH minutes (Long)
                val durationMinutes = if (quiz.durationMinutes <= 0L) 1L else quiz.durationMinutes

                // ✅ totalQuestions dari field quiz.totalQuestions (Long)
                val totalQuestions = quiz.totalQuestions.toInt()

                _uiState.value = TestInformationUiState(
                    isLoading = false,
                    error = null,

                    quizId = quiz.id,
                    title = quiz.title,
                    imageUrl = quiz.bannerUrl.orEmpty(),
                    participantText = participantText,

                    author = QuizAuthorUi(
                        name = authorName,
                        avatarUrl = user?.avatarUrl.orEmpty()
                    ),
                    createdTimeMillis = quiz.createdAt?.toDate()?.time ?: 0L,

                    totalQuestions = totalQuestions,
                    durationMinutes = durationMinutes
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

package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.TeacherQuizAnalytics
import com.example.tubes.data.model.TeacherQuizAnalyticsSummary
import com.example.tubes.domain.repository.TeacherAnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherAnalyticsUiState(
    val isLoading: Boolean = true,

    val summary: TeacherQuizAnalyticsSummary? = null,

    val barChartData: List<TeacherQuizAnalytics> = emptyList(),

    val quizList: List<TeacherQuizAnalytics> = emptyList()
)


class TeacherAnalyticsViewModel(
    private val repository: TeacherAnalyticsRepository,
    private val authorId: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(TeacherAnalyticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "ANALYTICS_VM",
                    "loadAnalytics authorId=$authorId"
                )

                _uiState.update { it.copy(isLoading = true) }

                val quizzes =
                    repository.getQuizzesAnalyticsByAuthor(authorId)

                android.util.Log.d(
                    "ANALYTICS_VM",
                    "quizzes size=${quizzes.size}"
                )

                val barChartData =
                    quizzes.sortedByDescending { it.totalParticipants }

                val totalParticipants =
                    quizzes.sumOf { it.totalParticipants }

                val globalAverageScore =
                    if (quizzes.isNotEmpty())
                        quizzes.map { it.averageScore }.average()
                    else 0.0

                val summary = TeacherQuizAnalyticsSummary(
                    globalAverageScore = globalAverageScore,
                    globalParticipants = totalParticipants
                )

                val quizList =
                    quizzes
                        .sortedByDescending { it.totalParticipants }
                        .take(3)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = summary,
                        barChartData = barChartData,
                        quizList = quizList
                    )
                }

            } catch (e: Exception) {
                android.util.Log.e("ANALYTICS_VM", "VM crash", e)

                // 🔑 fallback UI
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = TeacherQuizAnalyticsSummary(0.0, 0),
                        barChartData = emptyList(),
                        quizList = emptyList()
                    )
                }
            }
        }
    }
}

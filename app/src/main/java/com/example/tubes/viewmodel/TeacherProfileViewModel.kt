package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.RecentQuizActivity
import com.example.tubes.data.model.TeacherProfileStats
import com.example.tubes.data.model.TeacherProfileUiState
import com.example.tubes.domain.repository.TeacherProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class TeacherProfileViewModel(
    private val repository: TeacherProfileRepository,
    private val authorId: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(TeacherProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {

            val profile =
                repository.getUserProfile(authorId)

            val quizzes =
                repository.getQuizzesByAuthor(authorId)

            val totalParticipants =
                quizzes.sumOf { it.totalParticipants }

            val recent =
                quizzes
                    .sortedByDescending { it.publishAt }
                    .take(2)

            // 5️⃣ Statistik quiz 7 hari terakhir
            val weeklyStats =
                buildWeeklyQuizStats(quizzes)

            // 6️⃣ Update UI State
            _uiState.update {
                it.copy(
                    isLoading = false,
                    fullName = profile.fullName,
                    avatarUrl = profile.avatarUrl,
                    stats = TeacherProfileStats(
                        totalQuizzes = quizzes.size,
                        totalParticipants = totalParticipants
                    ),
                    recentQuizzes = recent,
                    weeklyQuizCount = weeklyStats
                )
            }
        }
    }

    private fun buildWeeklyQuizStats(
        quizzes: List<RecentQuizActivity>
    ): List<Int> {

        val today = LocalDate.now()
        val last7Days =
            (0..6).map { today.minusDays((6 - it).toLong()) }

        val counter =
            last7Days.associateWith { 0 }.toMutableMap()

        quizzes.forEach { quiz ->

            val date = Instant
                .ofEpochSecond(quiz.publishAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            if (counter.containsKey(date)) {
                counter[date] = counter[date]!! + 1
            }
        }

        // urut dari D-6 sampai Today
        return last7Days.map { counter[it] ?: 0 }
    }
}

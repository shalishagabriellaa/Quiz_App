package com.example.tubes.data.model

data class TeacherProfileUiState(
    val isLoading: Boolean = true,
    val fullName: String = "",
    val avatarUrl: String? = null,
    val stats: TeacherProfileStats? = null,
    val recentQuizzes: List<RecentQuizActivity> = emptyList(),
    val weeklyQuizCount: List<Int> = emptyList() // size = 7
)



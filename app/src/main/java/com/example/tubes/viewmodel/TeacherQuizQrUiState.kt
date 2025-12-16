package com.example.tubes.viewmodel

data class TeacherQuizQrUiState(
    val quizCode: String = "",
    val expiredAtMillis: Long = 0L,
    val isLoading: Boolean = false
)


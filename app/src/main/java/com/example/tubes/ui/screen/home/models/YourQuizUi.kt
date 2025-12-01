package com.example.tubes.ui.screen.home.models

import com.google.firebase.Timestamp

data class YourQuizUi(
    val quizId: String,
    val title: String,
    val bannerUrl: String?,
    val questionsCount: Long,
    val lastScore: Long,
    val correctAnswers: Long,
    val totalQuestions: Long,
    val lastPlayedAt: Timestamp?
)

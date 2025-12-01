package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class UserQuizResult(
    val quizId: String = "",
    val quizTitle: String = "",
    val quizBannerUrl: String? = null,
    val questionsCount: Long = 0L,
    val lastScore: Long = 0L,
    val correctAnswers: Long = 0L,
    val totalQuestions: Long = 0L,
    val lastPlayedAt: Timestamp? = null
)

package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class UserQuizResult(
    val quizId: String = "",
    val quizTitle: String = "",
    val score: Int = 0,
    val percentage: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val submittedAt: Timestamp? = null
)



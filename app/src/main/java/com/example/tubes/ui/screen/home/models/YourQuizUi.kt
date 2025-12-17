package com.example.tubes.ui.screen.home.models

import com.google.firebase.Timestamp

data class YourQuizUi(
    val quizId: String,
    val title: String,
    val score: Int,
    val percentage: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val submittedAt: Timestamp?
)


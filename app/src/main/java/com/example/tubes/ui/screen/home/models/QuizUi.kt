package com.example.tubes.ui.screen.home.models

import com.google.firebase.Timestamp

data class QuizUi(
    val id: String,
    val title: String,
    val authorName: String,
    val questionsCount: Long,
    val bannerUrl: String?,
    val createdAt: Timestamp?,
    val authorAvatarUrl: String?,
    val attemptCount: Long
)

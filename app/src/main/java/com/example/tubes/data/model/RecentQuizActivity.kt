package com.example.tubes.data.model

data class RecentQuizActivity(
    val quizId: String,
    val title: String,
    val bannerUrl: String?,
    val totalParticipants: Int,
    val publishAt: Long
)

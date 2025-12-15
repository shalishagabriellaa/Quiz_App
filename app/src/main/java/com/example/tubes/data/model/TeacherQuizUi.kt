package com.example.tubes.data.model

data class TeacherQuizUi(
    val id: String,
    val title: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val totalQuestions: Int,
    val status: String,
    val totalParticipants: Int,
    val averageScore: Double,
    val createdAtMillis: Long?,
    val bannerUrl: String?
)

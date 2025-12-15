package com.example.tubes.data.model

data class TeacherQuizAnalytics(
    val quizId: String,
    val title: String,
    val averageScore: Double,
    val totalParticipants: Int
)
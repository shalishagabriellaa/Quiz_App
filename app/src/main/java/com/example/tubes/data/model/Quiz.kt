package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class Quiz(
    val id: String = "",

    val title: String = "",
    val description: String = "",

    val bannerUrl: String? = null,

    val authorId: String = "",

    val categoryId: String = "",
    val categoryName: String = "",

    val quizCode: String = "",

    val difficulty: String = "",

    val durationMinutes: Long = 0L,
    val passingGrade: Long = 0L,

    val totalQuestions: Long = 0L,
    val totalParticipants: Long = 0L,
    val averageScore: Double = 0.0,

    val status: String = "",

    val publishAt: Timestamp? = null,
    val finishAt: Timestamp? = null,

    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

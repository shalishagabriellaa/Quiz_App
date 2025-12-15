package com.example.tubes.data.model


import com.google.firebase.Timestamp

data class TeacherCreateQuizFirestore(
    val title: String = "",
    val description: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val authorId: String = "",

    val durationMinutes: Int = 0,
    val totalQuestions: Int = 0,
    val difficulty: String = "",
    val passingGrade: Int = 0,

    val publishAt: Timestamp? = null,
    val finishAt: Timestamp? = null,
    val status: String = "draft",


    val bannerUrl: String? = null,

    val totalParticipants: Int = 0,
    val averageScore: Double = 0.0,

    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

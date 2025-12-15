package com.example.tubes.data.model

import android.net.Uri

data class TeacherCreateQuizUi(
    val title: String = "",
    val description: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val durationMinutes: String = "",
    val totalQuestions: String = "",
    val difficulty: String = "",
    val passingGrade: String = "",
    val publishAtMillis: Long? = null,
    val finishAtMillis: Long? = null,
    val bannerUri: Uri? = null
)

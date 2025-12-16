package com.example.tubes.data.model

data class TeacherNotificationUi(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val quizId: String?,
    val isRead: Boolean,
    val createdAtMillis: Long
)

package com.example.tubes.data.model

data class UserNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val quizId: String? = null,
    val isRead: Boolean = false,
    val createdAt: java.util.Date? = null
)

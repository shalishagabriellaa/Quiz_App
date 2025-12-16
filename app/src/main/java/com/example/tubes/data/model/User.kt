package com.example.tubes.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val avatarUrl: String? = null,

    val role: String = "user", // "user" / "author"

    val followersCount: Long = 0L,
    val quizzesCount: Long = 0L,

    val totalScore: Long = 0L,
    val weeklyScore: Long = 0L,
    val weekOfYear: Int = 0,

    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

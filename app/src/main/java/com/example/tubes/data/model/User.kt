package com.example.tubes.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp


data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val role: String = "user",

    @ServerTimestamp
    val createdAt: Timestamp? = null,

    @ServerTimestamp
    val updatedAt : Timestamp? = null,

    val totalScore: Long = 0L

)

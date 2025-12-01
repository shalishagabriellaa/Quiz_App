package com.example.tubes.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp


data class User(
    val uid: String = "",
    val name: String? = null,
    val fullName: String? = null,
    val email: String = "",
    val avatarUrl: String? = null,
    val role: String = "user",

    @ServerTimestamp
    val createdAt: Timestamp? = null,

    @ServerTimestamp
    val updatedAt : Timestamp? = null,

    val totalScore: Long = 0L

)

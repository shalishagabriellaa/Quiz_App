package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class Quiz(
    val id: String = "",
    val title: String = "",
    val categoryId: String = "",
    val authorId: String = "",
    val questionCount: Long = 0L,
    val bannerUrl: String = "",
    val timer: Long = 0L,                 // waktu total dalam detik
    val quizCode: String = "",           // 6 digit (untuk join)
    val attemptCount: Long = 0L,         // untuk top picks
    val popularity: Long = 0L,           // mirip attemptCount
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null

)

package com.example.tubes.data.model

data class Quiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val authorId: String = "",
    val categoryId: String = "",
    val questionCount: Int = 0,
    val thumbnail: String = "",
    val trendingScore: Int = 0,
    val createdAt: Long = 0L
)

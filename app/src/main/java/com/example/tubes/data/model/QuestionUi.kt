package com.example.tubes.data.model

/**
 * Model yang dipakai UI (Compose)
 */
data class QuestionUi(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val imageUrl: String? = null
)

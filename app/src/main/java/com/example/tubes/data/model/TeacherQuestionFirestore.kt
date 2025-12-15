package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class TeacherQuestionFirestore(
    val index: Int = 0,
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val explanation: String = "",
    val imageUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)

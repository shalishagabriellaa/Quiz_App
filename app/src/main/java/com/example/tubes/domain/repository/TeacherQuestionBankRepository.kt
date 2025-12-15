package com.example.tubes.domain.repository

import com.example.tubes.data.model.Difficulty
import com.example.tubes.data.model.TeacherQuestionBank

interface TeacherQuestionBankRepository {
    suspend fun getTeacherQuestionBank(
        authorId: String,
        difficulty: Difficulty?,
        limit: Int
    ): List<TeacherQuestionBank>

    suspend fun deleteTeacherQuestion(
        quizId: String,
        questionId: String
    )
}
package com.example.tubes.domain.repository

import com.example.tubes.data.model.TeacherQuestionForm

interface TeacherQuestionRepository {
    suspend fun saveQuestion(
        quizId: String,
        index: Int,
        form: TeacherQuestionForm
    )

    suspend fun getQuestion(
        quizId: String,
        index: Int
    ): TeacherQuestionForm?
}
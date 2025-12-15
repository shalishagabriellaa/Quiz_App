package com.example.tubes.domain.repository

import com.example.tubes.data.model.TeacherCreateQuizUi
import com.example.tubes.data.model.TeacherQuizUi

interface TeacherQuizRepository {
    suspend fun getTeacherQuizzes(authorId: String): List<TeacherQuizUi>
    suspend fun deleteTeacherQuizzes(quizId: String)
    suspend fun getById(quizId: String): TeacherCreateQuizUi

    suspend fun createQuiz(
        authorId: String,
        quiz: TeacherCreateQuizUi,
        bannerUrl: String?
    ): String


    suspend fun updateQuiz(                               // ⬅️ TAMBAH
        quizId: String,
        quiz: TeacherCreateQuizUi,
        bannerUrl: String?
    )
}
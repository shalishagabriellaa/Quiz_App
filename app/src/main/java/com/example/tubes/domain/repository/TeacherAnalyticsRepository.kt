package com.example.tubes.domain.repository

import com.example.tubes.data.model.TeacherQuizAnalytics

interface TeacherAnalyticsRepository {
    suspend fun getQuizzesAnalyticsByAuthor(
        authorId: String
    ): List<TeacherQuizAnalytics>
}
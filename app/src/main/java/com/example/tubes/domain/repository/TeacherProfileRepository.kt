package com.example.tubes.domain.repository

import com.example.tubes.data.model.RecentQuizActivity
import com.example.tubes.data.model.TeacherUserProfile

interface TeacherProfileRepository {

    suspend fun getUserProfile(
        authorId: String
    ): TeacherUserProfile

    suspend fun getQuizzesByAuthor(
        authorId: String
    ): List<RecentQuizActivity>

}

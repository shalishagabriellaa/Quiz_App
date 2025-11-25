package com.example.tubes.domain.repository

import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User

interface HomeRepository {
    suspend fun getUser(uid: String): User?
    suspend fun getCategories(): List<Category>
    suspend fun getTrendingQuizzes(): List<Quiz>
    suspend fun getTopAuthors(): List<User>
    suspend fun findQuizIdByCode(quizCode: String): String?
    suspend fun getQuizzesByCategory(categoryId: String): List<Quiz>

}

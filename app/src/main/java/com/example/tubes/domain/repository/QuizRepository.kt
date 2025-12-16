package com.example.tubes.domain.repository

import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User

interface QuizRepository {
    suspend fun getQuizById(quizId: String): Quiz?
    suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi>
    suspend fun getUser(userId: String): User?

    suspend fun saveQuizAttempt(
        userId: String?,
        quizId: String,
        quizTitle: String,
        score: Int,
        percentage: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        maxScore: Int,
        pointsEarned: Int,
        userAnswersIndex: Map<String, Int>
    )

    suspend fun addUserPoints(userId: String, points: Int)
    suspend fun getLatestAttemptAnswers(userId: String, quizId: String): Map<String, Int>
    suspend fun hasUserPassedQuiz(userId: String, quizId: String): Boolean

}

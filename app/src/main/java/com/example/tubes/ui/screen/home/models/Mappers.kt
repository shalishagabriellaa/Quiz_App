package com.example.tubes.ui.screen.home.models

import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.data.model.UserQuizResult

fun Category.toUi(): CategoryUi = CategoryUi(
    id = categoryId,                // ✅ pakai slug, bukan docId random
    name = categoryName,
    bannerUrl = bannerUrl
)

fun Quiz.toUi(
    authorName: String,
    authorAvatarUrl: String?
): QuizUi = QuizUi(
    id = id,
    title = title,
    bannerUrl = bannerUrl,
    questionsCount = totalQuestions,          // ✅ dari schema baru
    createdAt = createdAt,
    authorName = authorName,
    authorAvatarUrl = authorAvatarUrl,
    attemptCount = totalParticipants          // ✅ dari schema baru (plays)
)

fun User.toAuthorUi(): AuthorUi = AuthorUi(
    fullName = if (fullName.isNotBlank()) fullName else "Unknown",
    avatarUrl = avatarUrl
)

fun UserQuizResult.toYourQuizUi(): YourQuizUi {
    return YourQuizUi(
        quizId = quizId,
        title = quizTitle,
        score = score,
        percentage = percentage,
        totalQuestions = totalQuestions,
        correctAnswers = correctAnswers,
        submittedAt = submittedAt
    )
}



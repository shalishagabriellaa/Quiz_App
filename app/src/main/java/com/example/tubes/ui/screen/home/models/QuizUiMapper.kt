package com.example.tubes.ui.screen.home.models

import com.example.tubes.data.model.Quiz

fun Quiz.toUi(
    authorName: String,
    authorAvatarUrl: String?,
    attemptCount: Long
): QuizUi {
    return QuizUi(
        id = this.id,
        title = this.title,
        authorName = authorName,
        questionsCount = this.totalQuestions,
        bannerUrl = this.bannerUrl,
        createdAt = this.createdAt,
        authorAvatarUrl = authorAvatarUrl,
        attemptCount = attemptCount
    )
}

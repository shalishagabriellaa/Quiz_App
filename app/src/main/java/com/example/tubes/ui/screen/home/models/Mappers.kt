package com.example.tubes.ui.screen.home.models

import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.google.firebase.Timestamp

fun Quiz.toUi(authorName: String): QuizUi {
    return QuizUi(
        id = id,
        title = title,
        authorName = authorName,
        questionsCount = questionCount.toString(),
        bannerUrl = bannerUrl,
        createdAt = createdAt      // ✅ pastikan field ini ada di data.model.Quiz
    )
}

fun Category.toUi(): CategoryUi {
    return CategoryUi(
        id = this.id,
        name = this.name,
        bannerUrl = this.bannerUrl
    )
}

fun User.toAuthorUi(): AuthorUi {
    return AuthorUi(
        fullName = this.fullName ?: this.name ?: "Unknown",
        avatarUrl = this.avatarUrl
    )
}

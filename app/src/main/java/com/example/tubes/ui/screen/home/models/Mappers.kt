package com.example.tubes.ui.screen.home.models

import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User

fun Quiz.toUi(authorName: String): QuizUi {
    return QuizUi(
        id = id,
        title = title,
        authorName = authorName,
        questionsCount = questionCount.toString(),
        bannerUrl = bannerUrl
    )
}

fun Category.toUi(): CategoryUi {
    return CategoryUi(
        name = this.name,
        bannerUrl = this.bannerUrl)

}

fun User.toAuthorUi(): AuthorUi {
    return AuthorUi(
        fullName = this.fullName ?: this.name ?: "Unknown",
        avatarUrl = this.avatarUrl
    )
}

package com.example.tubes.data.model

import com.google.firebase.Timestamp

data class Quiz(
    val id: String = "",

    val title: String = "",
    val description: String = "",

    val bannerUrl: String? = null,

    val authorId: String = "",

    val categoryId: String = "",
    val categoryName: String = "",

    val quizCode: String = "",

    val difficulty: String = "",

    val durationMinutes: Long = 0L,
    val passingGrade: Long = 0L,

    val totalQuestions: Long = 0L,
    val totalParticipants: Long = 0L,
    val averageScore: Double = 0.0,

    val status: String = "",

    val publishAt: Timestamp? = null,
    val finishAt: Timestamp? = null,

    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)



data class TeacherQuizListUiState(
    val quizzes: List<TeacherQuizUi> = emptyList(),
    val isLoading: Boolean = false,

    // DELETE
    val showDeleteDialog: Boolean = false,
    val quizToDeleteId: String? = null,
    val quizToDeleteTitle: String? = null,
    val isDeleting: Boolean = false,
    val error: String? = null
)


fun String.toAnswerIndex(): Int {
    return when {
        this.startsWith("A.", ignoreCase = true) -> 0
        this.startsWith("B.", ignoreCase = true) -> 1
        this.startsWith("C.", ignoreCase = true) -> 2
        this.startsWith("D.", ignoreCase = true) -> 3
        else -> 0
    }
}
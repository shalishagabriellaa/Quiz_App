package com.example.tubes.data.model

data class TeacherCreateQuizValidation(
    val titleError: String? = null,
    val categoryError: String? = null,
    val durationError: String? = null,
    val totalQuestionsError: String? = null,
    val difficultyError: String? = null,
    val passingGradeError: String? = null,
    val publishDateError: String? = null,
    val finishDateError: String? = null
) {
    val isValid: Boolean
        get() = listOf(
            titleError,
            categoryError,
            durationError,
            totalQuestionsError,
            difficultyError,
            passingGradeError,
            publishDateError,
            finishDateError
        ).all { it == null }
}

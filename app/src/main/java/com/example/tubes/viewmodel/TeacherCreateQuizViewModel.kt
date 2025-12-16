package com.example.tubes.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.TeacherCreateQuizUi
import com.example.tubes.data.model.TeacherCreateQuizValidation
import com.example.tubes.domain.repository.CloudinaryRepository
import com.example.tubes.domain.repository.TeacherQuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherCreateQuizViewModel(
    private val quizRepository: TeacherQuizRepository,
    private val cloudinaryRepository: CloudinaryRepository,
    private val quizId: String? = null
) : ViewModel() {

    private val _form = MutableStateFlow(TeacherCreateQuizUi())
    val form: StateFlow<TeacherCreateQuizUi> = _form

    private val _validation =
        MutableStateFlow(TeacherCreateQuizValidation())
    val validation: StateFlow<TeacherCreateQuizValidation> = _validation

    val isEditMode: Boolean
        get() = quizId != null

    init {
        if (quizId != null) {
            loadQuiz(quizId)
        }
    }

    fun updateForm(newForm: TeacherCreateQuizUi) {
        _form.value = newForm
        _validation.value = TeacherCreateQuizValidation() // reset error saat ngetik
    }

    private fun loadQuiz(id: String) {
        viewModelScope.launch {
            val quiz = quizRepository.getById(id)
            _form.value = quiz
        }
    }

    private fun validateForm(): Boolean {
        val f = _form.value

        val publishError =
            when {
                f.publishAtMillis == null ->
                    "Publish date is required"
                else -> null
            }

        val finishError =
            when {
                f.finishAtMillis == null ->
                    "Finish date is required"

                f.publishAtMillis != null &&
                        f.finishAtMillis <= f.publishAtMillis ->
                    "Finish date must be after publish date"

                else -> null
            }

        val validation = TeacherCreateQuizValidation(

            // ================= TITLE =================
            titleError =
                if (f.title.isBlank())
                    "Title is required"
                else null,

            // ================= CATEGORY =================
            categoryError =
                if (f.categoryId.isBlank())
                    "Category is required"
                else null,

            // ================= DURATION =================
            durationError =
                when {
                    f.durationMinutes.isBlank() ->
                        "Duration is required"

                    f.durationMinutes.toIntOrNull() == null ->
                        "Must be a number"

                    f.durationMinutes.toInt() <= 0 ->
                        "Must be > 0"

                    else -> null
                },

            // ================= TOTAL QUESTIONS =================
            totalQuestionsError =
                when {
                    f.totalQuestions.isBlank() ->
                        "Total questions is required"

                    f.totalQuestions.toIntOrNull() == null ->
                        "Must be a number"

                    f.totalQuestions.toInt() <= 0 ->
                        "Must be > 0"

                    else -> null
                },

            // ================= DIFFICULTY =================
            difficultyError =
                if (f.difficulty.isBlank())
                    "Difficulty is required"
                else null,

            // ================= PASSING GRADE =================
            passingGradeError =
                when {
                    f.passingGrade.isBlank() ->
                        "Passing grade is required"

                    f.passingGrade.toIntOrNull() == null ->
                        "Must be a number"

                    f.passingGrade.toInt() !in 0..100 ->
                        "Must be 0–100"

                    else -> null
                },

            // ================= DATES =================
            publishDateError = publishError,
            finishDateError = finishError
        )

        _validation.value = validation
        return validation.isValid
    }

    fun submit(
        authorId: String,
        onSuccess: (quizId: String) -> Unit
    ) {
        viewModelScope.launch {

            if (!validateForm()) return@launch

            try {
                val bannerUrl = _form.value.bannerUri?.let {
                    cloudinaryRepository.uploadImage(it)
                }

                if (quizId == null) {
                    val newQuizId = quizRepository.createQuiz(
                        authorId = authorId,
                        quiz = _form.value,
                        bannerUrl = bannerUrl
                    )
                    onSuccess(newQuizId)
                } else {
                    quizRepository.updateQuiz(
                        quizId = quizId,
                        quiz = _form.value,
                        bannerUrl = bannerUrl
                    )
                    onSuccess(quizId)
                }

            } catch (e: Exception) {
                Log.e("TeacherCreateQuizVM", "Submit failed", e)
            }
        }
    }
}

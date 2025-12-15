package com.example.tubes.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.TeacherCreateQuizUi
import com.example.tubes.domain.repository.CloudinaryRepository
import com.example.tubes.domain.repository.TeacherQuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherCreateQuizViewModel(
    private val quizRepository: TeacherQuizRepository,
    private val cloudinaryRepository: CloudinaryRepository,
    private val quizId: String? = null
): ViewModel() {

    private val _form = MutableStateFlow(TeacherCreateQuizUi())
    val form: StateFlow<TeacherCreateQuizUi> = _form

    val isEditMode: Boolean
        get() = quizId != null

    fun updateForm(newForm: TeacherCreateQuizUi) {
        _form.value = newForm
    }

    init {
        if (quizId != null) {
            Log.d("EDIT_DEBUG", "VM INIT quizId=$quizId")
            loadQuiz(quizId)
        }
    }


    private fun loadQuiz(id: String) {
        viewModelScope.launch {
            Log.d("EDIT_DEBUG", "LOAD QUIZ id=$id")
            val quiz = quizRepository.getById(id)
            _form.value = quiz
        }
    }

    fun submit(
        authorId: String,
        onSuccess: (quizId: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val bannerUrl = _form.value.bannerUri?.let { uri ->
                    cloudinaryRepository.uploadImage(uri)
                }

                if (quizId == null) {
                    // 🆕 CREATE
                    val newQuizId = quizRepository.createQuiz(
                        authorId = authorId,
                        quiz = _form.value,
                        bannerUrl = bannerUrl
                    )
                    onSuccess(newQuizId)
                } else {
                    // ✏️ UPDATE
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

package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.TeacherQuestionRepository

class TeacherAddQuestionViewModelFactory(
    private val quizId: String,
    private val totalQuestions: Int,
    private val repo: TeacherQuestionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TeacherAddQuestionViewModel(
            quizId,
            totalQuestions,
            repo
        ) as T
    }
}




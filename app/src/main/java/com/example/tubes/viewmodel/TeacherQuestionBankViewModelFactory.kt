package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.TeacherQuestionBankRepository

class TeacherQuestionBankViewModelFactory(
    private val repository: TeacherQuestionBankRepository,
    private val authorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherQuestionBankViewModel::class.java)) {
            return TeacherQuestionBankViewModel(
                repository = repository,
                authorId = authorId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


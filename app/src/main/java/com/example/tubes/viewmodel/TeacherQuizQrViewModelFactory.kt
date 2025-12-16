package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.TeacherQuizRepository

class TeacherQuizQrViewModelFactory(
    private val quizRepository: TeacherQuizRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TeacherQuizQrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherQuizQrViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.TeacherQuizRepository

class TeacherQuizListViewModelFactory(
    private val repository: TeacherQuizRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherQuizListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherQuizListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

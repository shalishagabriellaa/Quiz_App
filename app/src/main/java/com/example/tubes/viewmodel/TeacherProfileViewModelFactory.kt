package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.TeacherProfileRepository

class TeacherProfileViewModelFactory(
    private val repository: TeacherProfileRepository,
    private val authorId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TeacherProfileViewModel::class.java)) {
            return TeacherProfileViewModel(
                repository = repository,
                authorId = authorId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

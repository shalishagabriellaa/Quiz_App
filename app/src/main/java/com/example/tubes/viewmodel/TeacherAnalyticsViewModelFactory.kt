package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.TeacherAnalyticsRepository

class TeacherAnalyticsViewModelFactory(
    private val repository: TeacherAnalyticsRepository,
    private val authorId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TeacherAnalyticsViewModel::class.java)) {
            return TeacherAnalyticsViewModel(
                repository = repository,
                authorId = authorId
            ) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}

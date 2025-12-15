package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.CloudinaryRepository
import com.example.tubes.domain.repository.TeacherQuizRepository

class TeacherCreateQuizViewModelFactory(
    private val quizRepository: TeacherQuizRepository,
    private val cloudinaryRepository: CloudinaryRepository,
    private val quizId: String? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("EDIT_DEBUG", "FACTORY USED quizId=$quizId")
        if (modelClass.isAssignableFrom(TeacherCreateQuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherCreateQuizViewModel(
                quizRepository,
                cloudinaryRepository,
                quizId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

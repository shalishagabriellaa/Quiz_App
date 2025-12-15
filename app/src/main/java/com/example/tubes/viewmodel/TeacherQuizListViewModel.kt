package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.TeacherQuizUi
import com.example.tubes.domain.repository.TeacherQuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherQuizListViewModel(
    private val repository: TeacherQuizRepository,
) : ViewModel() {

    private val _quizzes = MutableStateFlow<List<TeacherQuizUi>>(emptyList())
    val quizzes: StateFlow<List<TeacherQuizUi>> = _quizzes

    // 🔑 TAMBAHAN BARU
    private val _statusCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val statusCounts: StateFlow<Map<String, Int>> = _statusCounts

    fun load(authorId: String) {
        viewModelScope.launch {
            val result = repository.getTeacherQuizzes(authorId)

            _quizzes.value = result

            // 🔥 HITUNG STATUS DI SINI
            _statusCounts.value = result
                .groupingBy { it.status }
                .eachCount()
        }
    }

    fun delete(quizId: String, authorId: String) {
        viewModelScope.launch {
            repository.deleteTeacherQuizzes(quizId)
            load(authorId) // auto refresh
        }
    }
}

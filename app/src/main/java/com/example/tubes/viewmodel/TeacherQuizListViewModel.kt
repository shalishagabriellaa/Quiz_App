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

    // =========================
    // QUIZ LIST
    // =========================
    private val _quizzes =
        MutableStateFlow<List<TeacherQuizUi>>(emptyList())
    val quizzes: StateFlow<List<TeacherQuizUi>> = _quizzes

    // =========================
    // STATUS COUNTS
    // =========================
    private val _statusCounts =
        MutableStateFlow<Map<String, Int>>(emptyMap())
    val statusCounts: StateFlow<Map<String, Int>> = _statusCounts

    // =========================
    // DELETE STATE
    // =========================
    data class DeleteState(
        val showDialog: Boolean = false,
        val quizId: String? = null,
        val quizTitle: String? = null,
        val isDeleting: Boolean = false
    )

    private val _deleteState =
        MutableStateFlow(DeleteState())
    val deleteState: StateFlow<DeleteState> = _deleteState

    // =========================
    // LOAD QUIZZES
    // =========================
    fun load(authorId: String) {
        viewModelScope.launch {
            val result = repository.getTeacherQuizzes(authorId)

            _quizzes.value = result

            _statusCounts.value = result
                .groupingBy { it.status }
                .eachCount()
        }
    }

    // =========================
    // DELETE FLOW
    // =========================
    fun onDeleteClick(quiz: TeacherQuizUi) {
        _deleteState.value = DeleteState(
            showDialog = true,
            quizId = quiz.id,
            quizTitle = quiz.title
        )
    }

    fun cancelDelete() {
        _deleteState.value = DeleteState()
    }

    fun confirmDelete(authorId: String) {
        val quizId = _deleteState.value.quizId ?: return

        viewModelScope.launch {
            _deleteState.value =
                _deleteState.value.copy(isDeleting = true)

            repository.deleteTeacherQuizzes(quizId)

            _deleteState.value = DeleteState()

            load(authorId) // refresh list
        }
    }
}


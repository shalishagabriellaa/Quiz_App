package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.Difficulty
import com.example.tubes.data.model.TeacherQuestionBank
import com.example.tubes.domain.repository.TeacherQuestionBankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherQuestionBankViewModel(
    private val repository: TeacherQuestionBankRepository,
    private val authorId: String
) : ViewModel() {

    private val _questions =
        MutableStateFlow<List<TeacherQuestionBank>>(emptyList())
    val questions = _questions.asStateFlow()

    private val _selectedDifficulty =
        MutableStateFlow<Difficulty?>(null)
    init {
        Log.d("BANK_DEBUG", "ViewModel init called")
        load()
    }
    fun load() {
        viewModelScope.launch {
            _questions.value = repository.getTeacherQuestionBank(
                authorId = authorId,
                difficulty = _selectedDifficulty.value,
                limit = 20
            )
        }
    }

    fun onDifficultySelected(difficulty: Difficulty?) {
        _selectedDifficulty.value = difficulty
        load()
    }

    fun delete(item: TeacherQuestionBank) {
        viewModelScope.launch {
            repository.deleteTeacherQuestion(
                quizId = item.quizId,
                questionId = item.id
            )
            load()
        }
    }
}

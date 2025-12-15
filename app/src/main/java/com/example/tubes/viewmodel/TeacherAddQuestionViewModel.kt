package com.example.tubes.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.TeacherQuestionForm
import com.example.tubes.domain.repository.TeacherQuestionRepository
import kotlinx.coroutines.launch

class TeacherAddQuestionViewModel(
    private val quizId: String,
    private val totalQuestionsInternal: Int,
    private val repo: TeacherQuestionRepository
) : ViewModel() {

    // 👉 expose read-only untuk UI
    val totalQuestions: Int
        get() = totalQuestionsInternal

    var currentIndex by mutableStateOf(1)
        private set

    var form by mutableStateOf(TeacherQuestionForm())
        private set

    // cache supaya bisa edit ulang
    private val cache = mutableStateMapOf<Int, TeacherQuestionForm>()

    // flag selesai (UNTUK UI)
    var finished by mutableStateOf(false)
        private set
    val previewQuestions: List<Pair<Int, TeacherQuestionForm>>
        get() = cache.toList().sortedBy { it.first }


    init {
        loadQuestion(1)
    }

    private fun validate(): String? {
        if (form.questionText.isBlank())
            return "Question text wajib diisi"

        if (form.options.any { it.isBlank() })
            return "Semua pilihan jawaban harus diisi"

        if (form.correctAnswerIndex !in 0..3)
            return "Pilih jawaban yang benar"

        return null
    }


    fun updateForm(newForm: TeacherQuestionForm) {
        form = newForm
        cache[currentIndex] = newForm
    }

    fun goTo(index: Int) {
        if (index !in 1..totalQuestionsInternal) return
        currentIndex = index
        loadQuestion(index)
    }

    private fun loadQuestion(index: Int) {
        // 1️⃣ ambil dari cache
        cache[index]?.let {
            form = it
            return
        }

        // 2️⃣ ambil dari firestore
        viewModelScope.launch {
            val saved = repo.getQuestion(quizId, index)
            form = saved ?: TeacherQuestionForm()
            if (saved != null) {
                cache[index] = saved
            }
        }
    }

    fun saveCurrent() {
        viewModelScope.launch {
            repo.saveQuestion(
                quizId = quizId,
                index = currentIndex,
                form = form
            )
            cache[currentIndex] = form
        }
    }

    fun nextOrFinish(onError: (String) -> Unit) {
        val error = validate()
        if (error != null) {
            onError(error)
            return
        }

        viewModelScope.launch {
            repo.saveQuestion(
                quizId = quizId,
                index = currentIndex,
                form = form
            )
            cache[currentIndex] = form

            if (currentIndex < totalQuestionsInternal) {
                currentIndex++
                loadQuestion(currentIndex)
            } else {
                finished = true
            }
        }
    }
}

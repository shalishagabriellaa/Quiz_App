package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.ui.teacher.TeacherViewAllType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TeacherViewAllUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val quizzes: List<QuizWithStats> = emptyList(),
    val submissions: List<ParticipantSubmission> = emptyList()
)

class TeacherViewAllViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(TeacherViewAllUiState())
    val uiState: StateFlow<TeacherViewAllUiState> = _uiState.asStateFlow()

    fun load(authorId: String?, type: TeacherViewAllType) {
        if (authorId.isNullOrEmpty()) {
            _uiState.update { it.copy(isLoading = false, error = "Author ID tidak valid.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, quizzes = emptyList(), submissions = emptyList()) }

        viewModelScope.launch {
            try {
                when (type) {
                    TeacherViewAllType.AVERAGE_SCORE -> loadAllQuizzes(authorId, sortByAverageDesc = true)
                    TeacherViewAllType.RECENT_QUIZ -> loadAllQuizzes(authorId, sortByAverageDesc = false)
                    TeacherViewAllType.PARTICIPANTS -> loadAllSubmissions(authorId)
                }

                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Gagal memuat data: ${e.localizedMessage}") }
            }
        }
    }

    private suspend fun loadAllQuizzes(authorId: String, sortByAverageDesc: Boolean) {
        // Ambil nama author untuk display
        val authorDoc = db.collection("users").document(authorId).get().await()
        val authorName = authorDoc.getString("fullName") ?: "Teacher"

        // Ambil semua kuis milik teacher ini
        val quizzesSnapshot = db.collection("quizzes")
            .whereEqualTo("authorId", authorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val quizzes = quizzesSnapshot.documents.map { doc ->
            val totalParticipants = (doc.getLong("totalParticipants") ?: 0L).toInt()
            val cumulative = (doc.getLong("cumulativeScore") ?: 0L).toDouble()
            val avg = if (totalParticipants > 0) cumulative / totalParticipants else 0.0

            QuizWithStats(
                quizId = doc.id,
                title = doc.getString("title") ?: "",
                // catatan: kalau kamu memang simpan questions di subcollection,
                // menghitungnya satu-satu akan mahal. Untuk ViewAll kita pakai field totalQuestions kalau ada.
                totalQuestions = (doc.getLong("totalQuestions") ?: 0L).toInt(),
                totalParticipants = totalParticipants,
                averageScore = avg,
                authorName = authorName,
                createdAt = doc.getDate("createdAt")
            )
        }

        val finalList = if (sortByAverageDesc) {
            quizzes.sortedByDescending { it.averageScore }
        } else {
            quizzes // recent = sudah order createdAt desc
        }

        _uiState.update { it.copy(quizzes = finalList) }
    }

    private suspend fun loadAllSubmissions(authorId: String) {
        // ✅ REKOMENDASI: pastikan quizResults docs punya field "authorId"
        // supaya bisa filter per teacher.
        val snapshot = db.collectionGroup("quizResults")
            .whereEqualTo("authorId", authorId) // <--- WAJIB agar "punya teacher ini"
            .orderBy("lastPlayedAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val subs = snapshot.documents.map { doc ->
            ParticipantSubmission(
                studentName = doc.getString("studentName") ?: "Unknown",
                quizTitle = doc.getString("quizTitle") ?: "",
                score = doc.getLong("score") ?: 0L,
                submittedAt = doc.getDate("submittedAt")
            )

        }

        _uiState.update { it.copy(submissions = subs) }
    }
}

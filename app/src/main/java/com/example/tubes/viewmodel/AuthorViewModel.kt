package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

// =====================================================
// DATA MODELS
// =====================================================

data class QuizWithStats(
    val quizId: String = "",
    val title: String = "",
    val totalQuestions: Int = 0,
    val totalParticipants: Int = 0,
    val averageScore: Double = 0.0,
    val authorName: String = "",
    val createdAt: Date? = null
)

data class ParticipantSubmission(
    val studentName: String,
    val quizTitle: String,
    val score: Long,
    val submittedAt: Date?
)

data class AuthorStats(
    val totalQuizzes: Int = 0,
    val totalParticipants: Int = 0,
    val averageQuizScore: Double = 0.0
)

data class TeacherDashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val overallStats: AuthorStats = AuthorStats(),
    val averageScoresPerQuiz: List<QuizWithStats> = emptyList(),
    val recentQuizzes: List<QuizWithStats> = emptyList(),
    val recentSubmissions: List<ParticipantSubmission> = emptyList(),
    val authorName: String = "Teacher",
    val unreadNotificationCount: Int = 0,

    // Pagination
    val isLoadingMore: Boolean = false,
    val hasMoreSubmissions: Boolean = true
)

// =====================================================
// VIEWMODEL
// =====================================================

class AuthorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState = _uiState.asStateFlow()

    private var lastSubmissionSnapshot: DocumentSnapshot? = null
    private val PAGE_SIZE = 10

    // =====================================================
    // LOAD DASHBOARD
    // =====================================================
    fun loadDashboardData(authorId: String?) {
        if (authorId.isNullOrEmpty()) {
            _uiState.update {
                it.copy(isLoading = false, error = "Author ID tidak valid")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        lastSubmissionSnapshot = null

        viewModelScope.launch {
            try {
                // ===== AUTHOR =====
                val authorDoc = db.collection("users").document(authorId).get().await()
                val authorName = authorDoc.getString("fullName") ?: "Teacher"

                // ===== QUIZZES =====
                val quizzesSnapshot = db.collection("quizzes")
                    .whereEqualTo("authorId", authorId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                if (quizzesSnapshot.isEmpty) {
                    _uiState.update {
                        it.copy(isLoading = false, authorName = authorName)
                    }
                    return@launch
                }

                // ===== PROCESS QUIZZES (ASYNC) =====
// ===== PROCESS QUIZZES (ASYNC) =====
                val quizResults = quizzesSnapshot.documents.map { doc ->
                    viewModelScope.async {
                        val questionsSnapshot = db.collection("quizzes")
                            .document(doc.id)
                            .collection("questions")
                            .get()
                            .await()

                        val totalParticipants =
                            (doc.getLong("totalParticipants") ?: 0L).toInt()

                        // ===== PERBAIKAN DI SINI =====
                        // Hapus pengambilan "cumulativeScore" yang tidak ada.
                        // Langsung ambil "averageScore" dari dokumen.
                        val averageScoreFromDoc = doc.getDouble("averageScore") ?: 0.0

                        object {
                            val quiz = QuizWithStats(
                                quizId = doc.id,
                                title = doc.getString("title") ?: "",
                                totalQuestions = questionsSnapshot.size(),
                                totalParticipants = totalParticipants,

                                // Gunakan nilai yang sudah diambil, tidak perlu hitung ulang.
                                averageScore = averageScoreFromDoc,

                                authorName = authorName,
                                createdAt = doc.getDate("createdAt")
                            )

                            val rawScore = averageScoreFromDoc * totalParticipants
                        }
                    }
                }.awaitAll()


                val allQuizzes = quizResults.map { it.quiz }
                val overallParticipants = allQuizzes.sumOf { it.totalParticipants }
                val overallScore = quizResults.sumOf { it.rawScore }

                val overallStats = AuthorStats(
                    totalQuizzes = allQuizzes.size,
                    totalParticipants = overallParticipants,
                    averageQuizScore =
                        if (overallParticipants > 0)
                            overallScore / overallParticipants
                        else 0.0
                )

                // ===== PARTICIPANT ACTIVITY (FIRST PAGE) =====
                val quizIds = allQuizzes.map { it.quizId }
                val submissions = loadParticipantSubmissions(quizIds, loadMore = false)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authorName = authorName,
                        overallStats = overallStats,
                        averageScoresPerQuiz = allQuizzes,
                        recentQuizzes = allQuizzes,
                        recentSubmissions = submissions,
                        hasMoreSubmissions = submissions.size == PAGE_SIZE
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Gagal memuat dashboard"
                    )
                }
            }
        }
    }

    // =====================================================
    // LOAD MORE PARTICIPANT ACTIVITY
    // =====================================================
    fun loadMoreSubmissions(authorQuizIds: List<String>) {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreSubmissions) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val more = loadParticipantSubmissions(authorQuizIds, loadMore = true)

            _uiState.update {
                it.copy(
                    isLoadingMore = false,
                    recentSubmissions = it.recentSubmissions + more,
                    hasMoreSubmissions = more.size == PAGE_SIZE
                )
            }
        }
    }

    // =====================================================
    // CORE: LOAD quiz_attempts (PAGINATED)
    // =====================================================
    private suspend fun loadParticipantSubmissions(
        quizIds: List<String>,
        loadMore: Boolean
    ): List<ParticipantSubmission> {

        if (quizIds.isEmpty()) return emptyList()

        var query = db.collection("quiz_attempts")
            .whereIn("quizId", quizIds.take(10)) // Firestore limit
            .orderBy("submittedAt", Query.Direction.DESCENDING)
            .limit(PAGE_SIZE.toLong())

        if (loadMore && lastSubmissionSnapshot != null) {
            query = query.startAfter(lastSubmissionSnapshot!!)
        }

        val snapshot = query.get().await()
        if (snapshot.isEmpty) return emptyList()

        lastSubmissionSnapshot = snapshot.documents.last()

        val userIds = snapshot.documents
            .mapNotNull { it.getString("userId") }
            .distinct()

        val userNameMap = mutableMapOf<String, String>()
        if (userIds.isNotEmpty()) {
            val usersSnapshot = db.collection("users")
                .whereIn(FieldPath.documentId(), userIds.take(10))
                .get()
                .await()

            usersSnapshot.documents.forEach {
                userNameMap[it.id] = it.getString("fullName") ?: "Unknown"
            }
        }

        return snapshot.documents.map { doc ->
            ParticipantSubmission(
                studentName = userNameMap[doc.getString("userId")] ?: "Unknown",
                quizTitle = doc.getString("quizTitle") ?: "",
                score = doc.getLong("score") ?: 0,
                submittedAt = doc.getDate("submittedAt")
            )
        }
    }
}

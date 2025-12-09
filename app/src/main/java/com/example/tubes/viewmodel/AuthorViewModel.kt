package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

// DATA CLASS UNTUK MENAMPUNG SEMUA DATA DASHBOARD (TETAP SAMA)
data class QuizWithStats(
    val quizId: String = "",
    val title: String = "",
    val totalQuestions: Int = 0,
    val totalParticipants: Int = 0,
    val averageScore: Double = 0.0,
    val authorName: String = "",
    val createdAt: java.util.Date? = null
)

// PERUBAHAN: DATA CLASS INI SEKARANG MEREFLEKSIKAN STRUKTUR 'quizResults' ANDA
data class ParticipantSubmission(
    val studentName: String = "Unknown", // Kita asumsikan nama diambil dari tempat lain
    val quizTitle: String = "",
    val score: Long = 0,
    val finishedAt: java.util.Date? = null // Diambil dari 'lastPlayedAt'
)

// STATE UTAMA UNTUK DASHBOARD (TETAP SAMA)
data class TeacherDashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val overallStats: AuthorStats = AuthorStats(),
    val averageScoresPerQuiz: List<QuizWithStats> = emptyList(),
    val recentQuizzes: List<QuizWithStats> = emptyList(),
    val recentSubmissions: List<ParticipantSubmission> = emptyList(),
    val authorName: String = "Teacher"
)

// Data class AuthorStats tetap sama
data class AuthorStats(
    val totalQuizzes: Int = 0,
    val totalParticipants: Int = 0,
    val averageQuizScore: Double = 0.0
)


class AuthorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    fun loadDashboardData(authorId: String?) {
        if (authorId.isNullOrEmpty()) {
            _uiState.update { it.copy(isLoading = false, error = "Author ID tidak valid.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val authorDocument = db.collection("users").document(authorId).get().await()
                val authorName = authorDocument.getString("fullName") ?: "Teacher"
                android.util.Log.d("AuthorViewModel_Debug", "Dokumen user ditemukan? -> ${authorDocument.exists()}")
                val quizzesSnapshot = db.collection("quizzes")
                    .whereEqualTo("authorId", authorId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                if (quizzesSnapshot.isEmpty) {
                    _uiState.update { it.copy(isLoading = false, authorName = authorName) }
                    return@launch
                }

// ... (setelah quizzesSnapshot diambil) ...

// PERUBAIKAN BESAR: Kita akan memproses setiap kuis secara asinkron
// untuk mendapatkan jumlah pertanyaan dari subkoleksi
                val allQuizzesWithRawScores = quizzesSnapshot.documents.map { doc ->
                    // Jalankan coroutine baru untuk setiap kuis
                    viewModelScope.async {
                        // HITUNG PERTANYAAN DARI SUBKOLEKSI
                        val questionsSnapshot = db.collection("quizzes").document(doc.id)
                            .collection("questions").get().await()
                        val questionCount = questionsSnapshot.size() // <-- Ini jumlah pertanyaan sebenarnya

                        // Gunakan object anonymous untuk membawa data mentah
                        object {
                            val stats = QuizWithStats(
                                quizId = doc.id,
                                title = doc.getString("title") ?: "",
                                totalQuestions = questionCount, // <-- GUNAKAN HASIL HITUNGAN
                                totalParticipants = (doc.getLong("totalParticipants") ?: 0L).toInt(),
                                averageScore = run {
                                    val cumulative = (doc.getLong("cumulativeScore") ?: 0L).toDouble()
                                    val participants = (doc.getLong("totalParticipants") ?: 0L).toInt()
                                    if (participants > 0) cumulative / participants else 0.0
                                },
                                authorName = authorName,
                                createdAt = doc.getDate("createdAt")
                            )
                            val rawCumulativeScore = (doc.getLong("cumulativeScore") ?: 0L).toDouble()
                        }
                    }
                }.awaitAll() // Tunggu semua perhitungan pertanyaan selesai

// Buat list yang bersih hanya berisi data untuk UI
                val allQuizzes = allQuizzesWithRawScores.map { it.stats }

// 3. Hitung Statistik Keseluruhan (Logika ini tetap sama)
                val overallTotalParticipants = allQuizzes.sumOf { it.totalParticipants }
                val overallCumulativeScore = allQuizzesWithRawScores.sumOf { it.rawCumulativeScore }

// --- DEBUGGING LOGS (Boleh dihapus jika sudah tidak perlu) ---
                android.util.Log.d("AuthorViewModel_Debug", "Total Skor Kumulatif Mentah: $overallCumulativeScore")
                android.util.Log.d("AuthorViewModel_Debug", "Total Peserta Mentah: $overallTotalParticipants")
// --- END DEBUGGING LOGS ---

                val averageScoreResult = if (overallTotalParticipants > 0) overallCumulativeScore / overallTotalParticipants else 0.0

// --- DEBUGGING LOGS (Boleh dihapus jika sudah tidak perlu) ---
                android.util.Log.d("AuthorViewModel_Debug", "Hasil Rata-rata Skor (ViewModel): $averageScoreResult")

                val overallStats = AuthorStats(
                    totalQuizzes = allQuizzes.size,
                    totalParticipants = overallTotalParticipants,
                    averageQuizScore = averageScoreResult
                )

// 4. Ambil 3 Kuis untuk "Average Score per Quiz" (Tetap sama)
                val averageScoresPerQuiz = allQuizzes.take(3)

// 5. PERUBAIKAN LOGIKA: Ambil 3 Kuis Terbaru, tanpa filter waktu
                val recentQuizzes = allQuizzes.take(3)

// 6. Ambil aktivitas peserta terbaru (Logika ini tetap sama)
                var recentSubmissions: List<ParticipantSubmission> = emptyList()
                try {
                    val recentSubmissionsSnapshot = db.collectionGroup("quizResults")
                        .orderBy("lastPlayedAt", Query.Direction.DESCENDING)
                        .limit(3)
                        .get()
                        .await()

                    recentSubmissions = recentSubmissionsSnapshot.documents.map { doc ->
                        ParticipantSubmission(
                            quizTitle = doc.getString("quizTitle") ?: "Judul Kuis Tidak Ada",
                            score = doc.getLong("lastScore") ?: 0,
                            finishedAt = doc.getDate("lastPlayedAt")
                        )
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AuthViewModel_QuizResults", "Error fetching quizResults: ${e.message}")
                }

// Final Update ke UI State
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authorName = authorName,
                        overallStats = overallStats,
                        averageScoresPerQuiz = averageScoresPerQuiz,
                        recentQuizzes = recentQuizzes,
                        recentSubmissions = recentSubmissions,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Gagal memuat data: ${e.localizedMessage}")
                }
            }
        }
    }
}

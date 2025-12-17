package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.QuestionUi
import com.example.tubes.domain.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val quizTitle: String = "",
    val difficulty: String = "",
    val passingGrade: Long = 0L,
    val durationSeconds: Int = 0,

    val questions: List<QuestionUi> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, Int> = emptyMap(),

    val timeRemaining: Int = 0,
    val isSubmitted: Boolean = false,

    val correctAnswers: Int = 0,
    val scorePercent: Int = 0,
    val pointsEarned: Int = 0,
    val isPassed: Boolean = false,

    val canRetry: Boolean = true
)

class QuizViewModel(
    private val repo: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var hasAutoSubmitted = false
    private var currentQuizId: String = ""

// ... (kode lainnya tetap sama)

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            // JANGAN MUAT ULANG KUIS YANG SAMA JIKA SUDAH AKTIF dan BELUM DI-SUBMIT
            // Ini akan mencegah reset yang tidak perlu jika ada recomposition
            if (currentQuizId == quizId && !_uiState.value.isSubmitted) return@launch

            try {
                // ===== PERBAIKAN UTAMA: RESET STATE DI SINI =====
                // Ini memastikan kita memulai dari state yang bersih setiap kali kuis baru dimuat.
                // Kita set isLoading = true agar UI menampilkan loading indicator.
                _uiState.value = QuizUiState(isLoading = true)
                // ===========================================

                currentQuizId = quizId
                hasAutoSubmitted = false
                timerJob?.cancel()

                val quiz = repo.getQuizById(quizId) ?: throw Exception("Quiz not found")
                val questions = repo.getQuestionsByQuizId(quizId)

                val durationSeconds = (quiz.durationMinutes * 60L).toInt().coerceAtLeast(60)

                // Sekarang kita hanya perlu meng-update state yang sudah bersih
                // dengan data kuis yang baru menggunakan .copy()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null,
                    quizTitle = quiz.title,
                    difficulty = quiz.difficulty,
                    passingGrade = quiz.passingGrade,
                    durationSeconds = durationSeconds,
                    timeRemaining = durationSeconds,
                    questions = questions
                )

                startTimer()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

// ... (sisa kode tidak perlu diubah)


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val s = _uiState.value
                if (s.isSubmitted) return@launch

                val t = s.timeRemaining
                if (t <= 0) {
                    if (!hasAutoSubmitted) {
                        hasAutoSubmitted = true
                        submitQuiz(userId = null, auto = true)
                    }
                    return@launch
                }
                _uiState.value = s.copy(timeRemaining = t - 1)
            }
        }
    }

    fun selectAnswer(selectedIndex: Int) {
        val s = _uiState.value
        val q = s.questions.getOrNull(s.currentQuestionIndex) ?: return
        _uiState.value = s.copy(userAnswers = s.userAnswers + (q.id to selectedIndex))
    }

    fun nextQuestion() {
        val s = _uiState.value
        if (s.currentQuestionIndex < s.questions.size - 1) {
            _uiState.value = s.copy(currentQuestionIndex = s.currentQuestionIndex + 1)
        }
    }

    fun previousQuestion() {
        val s = _uiState.value
        if (s.currentQuestionIndex > 0) {
            _uiState.value = s.copy(currentQuestionIndex = s.currentQuestionIndex - 1)
        }
    }

    fun submitQuiz(userId: String?, auto: Boolean = false) {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.isSubmitted) return@launch

            timerJob?.cancel()

            val total = s.questions.size.coerceAtLeast(1)
            val correct = s.questions.count { q ->
                val userIndex = s.userAnswers[q.id]
                userIndex != null && userIndex == q.correctAnswerIndex
            }

            val percentage = ((correct.toDouble() / total.toDouble()) * 100.0).toInt()
            val passed = percentage.toLong() >= s.passingGrade
            val points = if (passed) difficultyToPoints(s.difficulty) else 0

            // save attempt (jawaban user disimpan buat explanation)
            try {
                repo.saveQuizAttempt(
                    userId = userId,
                    quizId = currentQuizId,
                    quizTitle = s.quizTitle,
                    score = percentage,          // kamu di attempt pakai "score" = 80 dst
                    percentage = percentage,
                    correctAnswers = correct,
                    totalQuestions = total,
                    maxScore = 100,
                    pointsEarned = points,
                    userAnswersIndex = s.userAnswers
                )

                // add points to user only if passed
                if (!userId.isNullOrBlank() && points > 0) {
                    repo.addUserPoints(userId, points)
                }
            } catch (_: Exception) {
                // tetap lanjut UI
            }

            val alreadyPassedBefore = try {
                if (!userId.isNullOrBlank()) repo.hasUserPassedQuiz(userId, currentQuizId) else false
            } catch (e: Exception) {
                false // fallback biar gak crash
            }

// Kalau user sudah pernah pass, canRetry = false
            val canRetry = !(alreadyPassedBefore || passed)
// ✅ artinya: kalau sekarang pass → false, kalau dulu sudah pass → false

            _uiState.value = s.copy(
                isSubmitted = true,
                correctAnswers = correct,
                scorePercent = percentage,
                isPassed = passed,
                pointsEarned = points,
                canRetry = canRetry,
                timeRemaining = 0
            )

        }
    }

    private fun difficultyToPoints(diff: String): Int {
        return when (diff.trim().lowercase()) {
            "easy" -> 10
            "medium" -> 20
            "hard" -> 40
            "extreme" -> 70
            else -> 0
        }
    }

    fun resetQuiz() {
        timerJob?.cancel()
        hasAutoSubmitted = false
        currentQuizId = ""
        _uiState.value = QuizUiState()
    }
}

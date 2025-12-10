package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.QuestionUi // Pastikan ini di-import
import com.example.tubes.data.model.QuizUiState
import com.example.tubes.data.repository.QuizRepository
import com.example.tubes.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    // Kita tidak perlu lagi membuat instance default di sini, biarkan Factory yang bekerja
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    companion object {
        private const val TAG = "QuizViewModel"
    }

    // Fungsi loadQuiz() tidak ada perubahan signifikan, sudah benar.
    fun setCurrentUserId(userId: String?) {
        _uiState.update { it.copy(currentUserId = userId) }
    }

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading quiz: $quizId")
                _uiState.update { it.copy(isLoading = true, error = null) }

                val quiz = repository.getQuizById(quizId)
                if (quiz == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Quiz not found") }
                    return@launch
                }

                val questions = repository.getQuestionsByQuizId(quizId)
                if (questions.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "No questions available") }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        quiz = quiz,
                        questions = questions, // Tidak perlu di-map lagi
                        timeRemaining = quiz.timer.toInt(), // Pastikan 'timer' ada di model Quiz Anda
                        isLoading = false,
                        error = null,
                        // [PENTING] Reset jawaban lama saat memuat kuis baru
                        userAnswers = emptyMap()
                    )
                }

                startTimer()
                Log.d(TAG, "Quiz loaded successfully: ${questions.size} questions")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading quiz", e)
                _uiState.update { it.copy(isLoading = false, error = "Gagal memuat quiz: ${e.message}") }
            }
        }
    }

    // Fungsi startTimer() tidak perlu diubah.
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && !_uiState.value.isSubmitted) {
                delay(1000)
                _uiState.update { it.copy(timeRemaining = it.timeRemaining - 1) }
            }
            if (_uiState.value.timeRemaining == 0 && !_uiState.value.isSubmitted) {
                Log.d(TAG, "Time's up! Auto-submitting quiz")
                // Mengirim userId yang sesuai saat auto-submit, atau fallback
                submitQuiz(_uiState.value.currentUserId ?: "guestUser")
            }
        }
    }

    // --- [PERUBAHAN 1: Cara Memilih Jawaban Diubah Total] ---
    // Parameter diubah menjadi `answerIndex: Int` agar sesuai dengan UI Anda.
    // Logika di dalamnya diubah untuk menyimpan jawaban dalam format yang benar: Map<QuestionID, AnswerText>
    fun selectAnswer(answerIndex: Int) {
        val state = _uiState.value
        // Pastikan kita tidak crash jika pertanyaan tidak ada
        if (state.currentQuestionIndex >= state.questions.size) return

        val currentQuestion: QuestionUi = state.questions[state.currentQuestionIndex]
        val selectedAnswerText = currentQuestion.options[answerIndex]

        _uiState.update {
            it.copy(
                userAnswers = it.userAnswers + (currentQuestion.id to selectedAnswerText)
            )
        }
        Log.d(TAG, "Answer selected for question ${currentQuestion.id} -> $selectedAnswerText")
    }


    // Fungsi previousQuestion & nextQuestion tidak perlu diubah secara logika,
    // tapi kita hapus `selectedAnswer` karena state itu tidak lagi relevan.
    fun previousQuestion() {
        if (_uiState.value.currentQuestionIndex > 0) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex - 1) }
        }
    }

    fun nextQuestion() {
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size - 1) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        }
    }

    // --- [PERUBAHAN 2: Fungsi submitQuiz Dirombak Total] ---
    // Semua logika perhitungan skor DIHAPUS dari ViewModel.
    // Fungsi ini sekarang hanya mengirimkan jawaban ke Repository.
    fun submitQuiz(
        userId: String?,
        onComplete: () -> Unit = { } // Callback disederhanakan, tidak lagi mengirim skor.
    ) {
        // Ambil ID pengguna dari state jika tidak disediakan
        val finalUserId = userId ?: _uiState.value.currentUserId
        if (finalUserId == null) {
            Log.e(TAG, "Cannot submit quiz, userId is null.")
            _uiState.update { it.copy(error = "User ID tidak ditemukan, tidak bisa submit.") }
            return
        }

        viewModelScope.launch {
            try {
                timerJob?.cancel()
                val state = _uiState.value
                val quizId = state.quiz?.id

                if (quizId == null) {
                    _uiState.update { it.copy(error = "Quiz ID is missing, cannot submit.") }
                    return@launch
                }

                // TIDAK ADA LAGI PERHITUNGAN SKOR DI SINI.
                Log.d(TAG, "Submitting quiz '$quizId' for user '$finalUserId' with answers: ${state.userAnswers}")

                // Panggil repository dengan format yang BENAR.
                repository.submitQuizResult(
                    userId = finalUserId,
                    quizId = quizId,
                    userAnswers = state.userAnswers // Kirim Map jawaban pengguna
                )

                // Update UI untuk menandakan kuis telah selesai.
                _uiState.update {
                    it.copy(
                        isSubmitted = true,
                        // Skor tidak lagi dihitung di sini, jadi kita tidak tahu nilainya.
                        // Layar hasil harus mengambil skor terbaru dari `quizResults` di Firestore.
                        score = 0
                    )
                }

                Log.d(TAG, "Quiz submission request sent to repository successfully.")
                onComplete() // Panggil callback untuk navigasi

            } catch (e: Exception) {
                Log.e(TAG, "Error submitting quiz", e)
                _uiState.update { it.copy(error = "Failed to submit quiz: ${e.message}") }
            }
        }
    }

    // Fungsi resetQuiz dan onCleared tidak perlu diubah.
    fun resetQuiz() {
        _uiState.update { QuizUiState() }
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

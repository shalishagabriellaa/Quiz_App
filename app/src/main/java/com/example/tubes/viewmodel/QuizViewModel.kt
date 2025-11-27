package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: QuizRepository = QuizRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    companion object {
        private const val TAG = "QuizViewModel"
    }

    /**
     * Load quiz dan pertanyaan dari Firestore
     */
    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading quiz: $quizId")
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Ambil data quiz
                val quiz = repository.getQuizById(quizId)
                if (quiz == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Quiz not found"
                        )
                    }
                    return@launch
                }

                // Ambil pertanyaan
                val questions = repository.getQuestionsByQuizId(quizId)
                if (questions.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "No questions available"
                        )
                    }
                    return@launch
                }

                // Update state dengan data dari Firebase
                _uiState.update {
                    it.copy(
                        quiz = quiz,
                        questions = questions.map { q ->
                            q.copy(category = quiz.title)
                        },
                        timeRemaining = quiz.timer.toInt(),
                        isLoading = false,
                        error = null
                    )
                }

                // Mulai timer
                startTimer()

                Log.d(TAG, "Quiz loaded successfully: ${questions.size} questions")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading quiz", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Gagal memuat quiz: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Mulai timer countdown
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && !_uiState.value.isSubmitted) {
                delay(1000)
                _uiState.update {
                    it.copy(timeRemaining = it.timeRemaining - 1)
                }
            }

            // Auto submit jika waktu habis
            if (_uiState.value.timeRemaining == 0 && !_uiState.value.isSubmitted) {
                Log.d(TAG, "Time's up! Auto-submitting quiz")
                submitQuiz()
            }
        }
    }

    /**
     * Pilih jawaban untuk pertanyaan saat ini
     */
    fun selectAnswer(answerIndex: Int) {
        val currentIndex = _uiState.value.currentQuestionIndex
        _uiState.update { state ->
            state.copy(
                selectedAnswer = answerIndex,
                userAnswers = state.userAnswers + (currentIndex to answerIndex)
            )
        }
        Log.d(TAG, "Answer selected: question $currentIndex -> answer $answerIndex")
    }

    /**
     * Navigasi ke pertanyaan sebelumnya
     */
    fun previousQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex > 0) {
            val prevIndex = currentState.currentQuestionIndex - 1
            val prevAnswer = currentState.userAnswers[prevIndex]

            _uiState.update {
                it.copy(
                    currentQuestionIndex = prevIndex,
                    selectedAnswer = prevAnswer
                )
            }
            Log.d(TAG, "Moved to previous question: $prevIndex")
        }
    }

    /**
     * Navigasi ke pertanyaan berikutnya
     */
    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            val nextIndex = currentState.currentQuestionIndex + 1
            val nextAnswer = currentState.userAnswers[nextIndex]

            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = nextAnswer
                )
            }
            Log.d(TAG, "Moved to next question: $nextIndex")
        }
    }

    /**
     * Submit quiz dan hitung score
     */
    fun submitQuiz(
        userId: String? = null,
        onComplete: (Int, Int) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                timerJob?.cancel()

                val state = _uiState.value

                // Hitung jumlah jawaban benar
                var correctAnswers = 0
                state.questions.forEachIndexed { index, question ->
                    val userAnswer = state.userAnswers[index]
                    if (userAnswer == question.correctAnswerIndex) {
                        correctAnswers++
                    }
                }

                val totalQuestions = state.questions.size
                val scorePercent = (correctAnswers * 100) / totalQuestions

                // Hitung poin berdasarkan persentase
                val points = when {
                    scorePercent == 100 -> 10
                    scorePercent in 90..99 -> 9
                    scorePercent in 80..89 -> 8
                    scorePercent in 70..79 -> 7
                    scorePercent in 60..69 -> 6
                    scorePercent in 50..59 -> 5
                    scorePercent in 40..49 -> 4
                    scorePercent in 30..39 -> 3
                    scorePercent in 20..29 -> 2
                    else -> 1
                }

                Log.d(
                    TAG,
                    "Quiz submitted: $correctAnswers/$totalQuestions correct, " +
                            "score: $scorePercent, points: $points"
                )

                // Simpan hasil ke Firestore
                state.quiz?.let { quiz ->
                    val finalUserId = userId ?: "guestUser"
                    repository.submitQuizResult(
                        userId = finalUserId,
                        quizId = quiz.id,
                        score = points,              // <- sekarang yang dikirim adalah POINTS
                        totalQuestions = totalQuestions
                    )
                }

                // Update state untuk UI (scorePercent dipakai di layar hasil)
                _uiState.update {
                    it.copy(
                        isSubmitted = true,
                        score = scorePercent
                    )
                }

                // Callback dengan hasil
                onComplete(correctAnswers, totalQuestions)

            } catch (e: Exception) {
                Log.e(TAG, "Error submitting quiz", e)
                _uiState.update {
                    it.copy(error = "Failed to submit quiz: ${e.message}")
                }
            }
        }
    }

    /**
     * Reset quiz (untuk retry)
     */
    fun resetQuiz() {
        _uiState.update {
            QuizUiState()
        }
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
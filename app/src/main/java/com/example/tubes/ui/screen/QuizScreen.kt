package com.example.tubes.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data Models
data class Question(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)

data class QuizState(
    val currentQuestion: Int = 0,
    val totalQuestions: Int = 3,
    val timeRemaining: Int = 270, // 04:30 in seconds
    val selectedAnswer: Int? = null,
    val questions: List<Question> = emptyList()
)

// Dummy Data
val dummyQuestions = listOf(
    Question(
        id = 1,
        category = "Machine Learning",
        question = "Model digunakan untuk memprediksi apakah pesan termasuk spam atau tidak berdasarkan data berlabel. Jenis pembelajaran apa yang digunakan?",
        options = listOf(
            "Unsupervised learning",
            "Reinforcement learning",
            "Supervised learning",
            "Deep learning"
        ),
        correctAnswer = 2
    ),
    Question(
        id = 2,
        category = "Machine Learning",
        question = "Algoritma mana yang paling cocok untuk klasifikasi gambar?",
        options = listOf(
            "Linear Regression",
            "Decision Trees",
            "Convolutional Neural Network",
            "K-Means Clustering"
        ),
        correctAnswer = 2
    ),
    Question(
        id = 3,
        category = "Machine Learning",
        question = "Apa fungsi dari activation function dalam neural network?",
        options = listOf(
            "Mengurangi overfitting",
            "Menambah non-linearity",
            "Mempercepat training",
            "Mengurangi parameters"
        ),
        correctAnswer = 1
    )
)

// ViewModel
class QuizViewModel : ViewModel() {
    private val _quizState = MutableStateFlow(
        QuizState(questions = dummyQuestions, totalQuestions = dummyQuestions.size)
    )
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizState.value.timeRemaining > 0) {
                delay(1000)
                _quizState.update { it.copy(timeRemaining = it.timeRemaining - 1) }
            }
        }
    }

    fun selectAnswer(index: Int) {
        _quizState.update { it.copy(selectedAnswer = index) }
    }

    fun previousQuestion() {
        val currentState = _quizState.value
        if (currentState.currentQuestion > 0) {
            _quizState.update {
                it.copy(
                    currentQuestion = it.currentQuestion - 1,
                    selectedAnswer = null
                )
            }
        }
    }

    fun nextQuestion() {
        val currentState = _quizState.value
        if (currentState.currentQuestion < currentState.totalQuestions - 1) {
            _quizState.update {
                it.copy(
                    currentQuestion = it.currentQuestion + 1,
                    selectedAnswer = null
                )
            }
        }
    }

    fun submitQuiz() {
        // Handle quiz submission here
        timerJob?.cancel()
        // You can navigate to result screen or show dialog
        println("Quiz submitted!")
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

// Main Composable
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = viewModel()
) {
    val state by viewModel.quizState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A237E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            QuizTopBar(
                currentQuestion = state.currentQuestion + 1,
                totalQuestions = state.totalQuestions,
                timeRemaining = state.timeRemaining
            )

            // Progress Bar
            LinearProgressIndicator(
                progress = { (state.currentQuestion + 1).toFloat() / state.totalQuestions },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF4DFFF3),
                trackColor = Color(0xFF424242)
            )

            // Question Card
            if (state.questions.isNotEmpty()) {
                QuestionCard(
                    question = state.questions[state.currentQuestion],
                    selectedAnswer = state.selectedAnswer,
                    onAnswerSelected = { viewModel.selectAnswer(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp)
                )
            }

            // Navigation Buttons
            // Ganti bagian tombol Back & Submit di dalam Row navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Back
                if (state.currentQuestion > 0) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // biru tanpa border
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Back",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tombol Next/Submit
                Button(
                    onClick = {
                        if (state.currentQuestion == state.totalQuestions - 1) {
                            viewModel.submitQuiz()
                        } else {
                            viewModel.nextQuestion()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.currentQuestion == state.totalQuestions - 1)
                            Color(0xFF4CAF50) // hijau tanpa border
                        else
                            Color(0xFFFF6B9D),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = if (state.currentQuestion == state.totalQuestions - 1) "Submit" else "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}

@Composable
fun QuizTopBar(
    currentQuestion: Int,
    totalQuestions: Int,
    timeRemaining: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = { /* Handle back navigation */ },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF3949AB), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Question Counter
        Text(
            text = "%02d of %02d".format(currentQuestion, totalQuestions),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // Timer
        Row(
            modifier = Modifier
                .background(Color(0xFF5C6BC0), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Timer",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "%02d:%02d".format(timeRemaining / 60, timeRemaining % 60),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuestionCard(
    question: Question,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Category
            Text(
                text = question.category,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Question
            Text(
                text = question.question,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Options
            question.options.forEachIndexed { index, option ->
                OptionItem(
                    option = option,
                    optionLabel = "${'A' + index}.",
                    isSelected = selectedAnswer == index,
                    onClick = { onAnswerSelected(index) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OptionItem(
    option: String,
    optionLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val selectedColor = Color(0xFF0D47A1) // biru tua
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color(0xFFF5F5F5)
        ),
        border = if (isSelected) BorderStroke(2.dp, selectedColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = optionLabel,
                    color = if (isSelected) selectedColor else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )
                Text(
                    text = option,
                    color = if (isSelected) selectedColor else Color.DarkGray,
                    fontSize = 16.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = selectedColor,
                    unselectedColor = Color.Gray
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    QuizScreen()
}

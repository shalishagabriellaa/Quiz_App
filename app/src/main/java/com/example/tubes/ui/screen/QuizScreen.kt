package com.example.tubes.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.repository.QuizRepositoryImpl
import com.example.tubes.viewmodel.QuizViewModel
import androidx.compose.material.icons.filled.ArrowForward

@Composable
fun QuizScreen(
    quizId: String,
    onBackClick: () -> Unit = {},
    onQuizComplete: (score: Int, total: Int) -> Unit = { _, _ -> },
    onViewExplanation: (quizId: String) -> Unit = {},
    userId: String? = null
) {
    // Create ViewModel with factory
    val viewModel: QuizViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(QuizRepositoryImpl()) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    // Load quiz on first composition / when quizId changes
    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    // Loading state
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A237E)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading quiz...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    // Error state
    if (uiState.error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A237E)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Back")
                }
            }
        }
        return
    }

    // Submitted state → show result screen
    if (uiState.isSubmitted) {
        val correctAnswers = uiState.userAnswers.count { (index, answer) ->
            uiState.questions.getOrNull(index)?.correctAnswerIndex == answer
        }

        QuizResultScreen(
            quizId = quizId,
            score = uiState.score, // this is percentage
            correctAnswers = correctAnswers,
            totalQuestions = uiState.questions.size,
            onBackClick = onBackClick,
            onRetry = {
                viewModel.resetQuiz()
                viewModel.loadQuiz(quizId)
            },
            onViewExplanation = {
                onViewExplanation(quizId)
            }
        )
        return
    }

    // Main Quiz Screen
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
                currentQuestion = uiState.currentQuestionIndex + 1,
                totalQuestions = uiState.questions.size,
                timeRemaining = uiState.timeRemaining,
                onBackClick = onBackClick
            )

            // Progress Bar
            if (uiState.questions.isNotEmpty()) {
                LinearProgressIndicator(
                    progress = {
                        (uiState.currentQuestionIndex + 1).toFloat() / uiState.questions.size
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4DFFF3),
                    trackColor = Color(0xFF424242)
                )
            }

            // Question Card
            if (uiState.questions.isNotEmpty()) {
                QuestionCard(
                    question = uiState.questions[uiState.currentQuestionIndex],
                    selectedAnswer = uiState.selectedAnswer,
                    onAnswerSelected = { viewModel.selectAnswer(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp)
                )
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Back button (to previous question)
                if (uiState.currentQuestionIndex > 0) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
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

                // Next / Submit button
                Button(
                    onClick = {
                        if (uiState.currentQuestionIndex == uiState.questions.size - 1) {
                            // Last question → submit quiz
                            viewModel.submitQuiz(userId = userId ?: "guestUser") { correct, total ->
                                onQuizComplete(correct, total)
                            }
                        } else {
                            // Move to next question
                            viewModel.nextQuestion()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (uiState.currentQuestionIndex == uiState.questions.size - 1)
                                Color(0xFF4CAF50)
                            else
                                Color(0xFFFF6B9D),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = if (uiState.currentQuestionIndex == uiState.questions.size - 1)
                            "Submit"
                        else
                            "Next",
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
    timeRemaining: Int,
    onBackClick: () -> Unit
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
            onClick = onBackClick,
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
                .background(
                    if (timeRemaining < 60) Color(0xFFE53935) else Color(0xFF5C6BC0),
                    RoundedCornerShape(20.dp)
                )
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
    question: QuestionUi,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            // Category
            Text(
                text = question.category,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Question text
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
    val selectedColor = Color(0xFF0D47A1)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                selectedColor.copy(alpha = 0.15f)
            else
                Color(0xFFF5F5F5)
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

@Composable
fun QuizResultScreen(
    quizId: String,
    score: Int,              // percentage (0–100)
    correctAnswers: Int,
    totalQuestions: Int,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onViewExplanation: () -> Unit
) {
    val incorrectAnswers = totalQuestions - correctAnswers

    val (icon, message) = when {
        score in 90..100 -> "🔥" to "Excellent! You nailed it!"
        score in 80..89 -> "🌟" to "Great job! Almost perfect!"
        score in 70..79 -> "👍" to "Good work! Keep improving!"
        score in 60..69 -> "✨" to "Not bad! You're getting there!"
        else -> "💛" to "Don't give up! Try again—you can do it!"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A237E))
    ) {
        // Card utama di tengah
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dynamic icon
                Text(
                    text = icon,
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quiz Finished!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dynamic message
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your Score",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Text(
                    text = "$score%",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Correct: $correctAnswers",
                    fontSize = 18.sp,
                    color = Color(0xFF2E7D32)
                )

                Text(
                    text = "Incorrect: $incorrectAnswers",
                    fontSize = 18.sp,
                    color = Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Try Again button
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Try Again",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Back button
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(2.dp, Color(0xFF2196F3))
                ) {
                    Text(
                        text = "Back",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
            }
        }

        // Tombol "View Explanations" di luar card, bawah kanan
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .clickable { onViewExplanation() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View Explanations",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View explanations",
                tint = Color.White
            )
        }
    }
}

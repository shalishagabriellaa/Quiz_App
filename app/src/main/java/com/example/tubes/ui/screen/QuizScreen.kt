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
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.tubes.data.model.QuestionUi
import com.example.tubes.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    quizId: String,
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    // Callback disederhanakan: hanya memberitahu "kuis selesai"
    onQuizComplete: () -> Unit,
    onViewExplanation: (quizId: String) -> Unit,
    userId: String?
) {

    val uiState by viewModel.uiState.collectAsState()

    // Muat kuis saat pertama kali layar dibuat atau saat quizId berubah
    LaunchedEffect(quizId) {
        // Pemanggilan setCurrentUserId() dihapus karena tidak lagi diperlukan.
        // userId akan dikirim langsung saat submit.
        viewModel.loadQuiz(quizId)
    }

    // --- Bagian Loading & Error (Tidak ada perubahan) ---
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
                Text(text = "⚠️", fontSize = 48.sp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Back")
                }
            }
        }
        return
    }

    // --- Layar Hasil (Setelah Kuis Disubmit) ---
    if (uiState.isSubmitted) {
        // Layar hasil disederhanakan karena ViewModel tidak lagi menghitung skor.
        QuizResultScreen(
            quizId = quizId,
            onBackClick = onBackClick,
            onRetry = {
                // Reset state dan muat ulang kuis untuk mencoba lagi.
                viewModel.resetQuiz()
                viewModel.loadQuiz(quizId)
            },
            onViewExplanation = { onViewExplanation(quizId) }
        )
        return
    }

    // --- Layar Kuis Utama ---
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
                    progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4DFFF3),
                    trackColor = Color(0xFF424242)
                )
            }

            // Kartu Pertanyaan
            // Kartu Pertanyaan
            // Kartu Pertanyaan
            if (uiState.questions.isNotEmpty() && uiState.currentQuestionIndex < uiState.questions.size) {
                val currentQuestion = uiState.questions[uiState.currentQuestionIndex]

                // --- PERBAIKAN DI SINI ---    // Dapatkan jawaban yang dipilih dari map (bisa jadi null)
                val selectedAnswer = uiState.userAnswers[currentQuestion.id]
                // Cari indeks dari jawaban tersebut. Jika 'selectedAnswer' adalah null, indexOf akan aman dan mengembalikan -1.
                val answerIndex = currentQuestion.options.indexOf(selectedAnswer)

                QuestionCard(
                    question = currentQuestion,
                    selectedAnswerIndex = answerIndex, // Gunakan hasil yang sudah aman (answerIndex)
                    onAnswerSelected = { newAnswerIndex -> viewModel.selectAnswer(newAnswerIndex) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp)
                )
            }


            // Tombol Navigasi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Back (pertanyaan sebelumnya)
                if (uiState.currentQuestionIndex > 0) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Back", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = {
                        if (uiState.currentQuestionIndex == uiState.questions.size - 1) {
                            viewModel.submitQuiz(
                                userId = userId,
                            )
                        } else {
                            viewModel.nextQuestion()
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.currentQuestionIndex == uiState.questions.size - 1) Color(0xFF4CAF50) else Color(0xFFFF6B9D),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = if (uiState.currentQuestionIndex == uiState.questions.size - 1) "Submit" else "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- Composable lainnya tidak perlu diubah, tapi disertakan agar lengkap ---

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
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp).background(Color(0xFF3949AB), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
        }
        Text(
            text = "%02d of %02d".format(currentQuestion, totalQuestions),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .background(
                    if (timeRemaining < 60) Color(0xFFE53935) else Color(0xFF5C6BC0),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timer, "Timer", tint = Color.White, modifier = Modifier.size(20.dp))
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
    selectedAnswerIndex: Int,
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(question.category, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(question.question, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
            Spacer(modifier = Modifier.height(24.dp))
            question.options.forEachIndexed { index, option ->
                OptionItem(
                    option = option,
                    optionLabel = "${'A' + index}.",
                    isSelected = selectedAnswerIndex == index,
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color(0xFFF5F5F5)),
        border = if (isSelected) BorderStroke(2.dp, selectedColor) else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = optionLabel,
                    color = if (isSelected) selectedColor else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )
                Text(
                    text = option,
                    color = if (isSelected) Color.DarkGray else Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = selectedColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    quizId: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onViewExplanation: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1A237E)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "✅", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quiz Submitted!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your results will be updated on your profile shortly.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Back to Home")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}

package com.example.tubes.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tubes.data.repository.QuizRepositoryImpl
import com.example.tubes.viewmodel.AnswerExplanationViewModel
import com.example.tubes.viewmodel.AnswerExplanationViewModelFactory

@Composable
fun AnswerExplanationScreen(
    quizId: String,
    onBackClick: () -> Unit
) {
    // ✅ pakai VM baru yang state-nya jelas
    val repo = remember { QuizRepositoryImpl() }
    val vm: AnswerExplanationViewModel = viewModel(
        factory = AnswerExplanationViewModelFactory(repo)
    )
    val uiState by vm.uiState.collectAsState()

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var expandedQuestions by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(quizId) {
        vm.load(quizId)
    }

    // Loading
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A237E)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
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
            Text(text = "Error: ${uiState.error}", color = Color.White)
        }
        return
    }

    val questions = uiState.questions
    if (questions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A237E)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No questions available.", color = Color.White)
        }
        return
    }

    if (currentQuestionIndex !in questions.indices) currentQuestionIndex = 0

    val totalQuestions = questions.size
    val currentQuestion = questions[currentQuestionIndex]
    val options = currentQuestion.options
    val correctAnswerIndex = currentQuestion.correctAnswerIndex

    // ✅ jawaban user sekarang pakai index, bukan text
    val userAnswerIndex: Int? = uiState.userAnswersIndex[currentQuestion.id]

    val isExpanded = expandedQuestions.contains(currentQuestionIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A237E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "${(currentQuestionIndex + 1).toString().padStart(2, '0')} of ${totalQuestions.toString().padStart(2, '0')}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF00BCD4),
                trackColor = Color(0xFF3949AB)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Question",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentQuestion.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    options.forEachIndexed { index, option ->
                        ExplanationOptionItem(
                            option = option,
                            prefix = ('A' + index).toString(),
                            isUserAnswer = (userAnswerIndex == index),
                            isCorrectAnswer = (index == correctAnswerIndex),
                            showResult = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    expandedQuestions = if (isExpanded) {
                        expandedQuestions - currentQuestionIndex
                    } else {
                        expandedQuestions + currentQuestionIndex
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isExpanded) "Hide Explanation ▲" else "Show Explanation ▼",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // tampilkan correct answer
                        val correctLabel = ('A' + correctAnswerIndex).toString()
                        Text(
                            text = "Correct Answer: $correctLabel. ${options.getOrNull(correctAnswerIndex).orEmpty()}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentQuestion.explanation,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (currentQuestionIndex > 0) currentQuestionIndex--
                        else onBackClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Previous", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (currentQuestionIndex < totalQuestions - 1) currentQuestionIndex++
                        else onBackClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (currentQuestionIndex == totalQuestions - 1) "Finish" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ExplanationOptionItem(
    option: String,
    prefix: String,
    isUserAnswer: Boolean,
    isCorrectAnswer: Boolean,
    showResult: Boolean
) {
    val backgroundColor = when {
        showResult && isCorrectAnswer -> Color(0xFFE8F5E9)
        showResult && isUserAnswer && !isCorrectAnswer -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    val borderColor = when {
        showResult && isCorrectAnswer -> Color(0xFF4CAF50)
        showResult && isUserAnswer && !isCorrectAnswer -> Color(0xFFF44336)
        else -> Color(0xFFE0E0E0)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "$prefix. ", fontSize = 16.sp, color = Color.Gray)
                Text(text = option, fontSize = 16.sp)
            }

            if (showResult) {
                when {
                    isCorrectAnswer -> Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )

                    isUserAnswer && !isCorrectAnswer -> Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Wrong",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

package com.example.tubes.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnswerExplanationScreen() {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showAnswer by remember { mutableStateOf(false) }
    var answeredQuestions by remember { mutableStateOf(setOf<Int>()) }

    // Dummy questions data
    val questions = listOf(
        mapOf(
            "category" to "Machine Learning",
            "question" to "Model digunakan untuk memprediksi apakah pesan termasuk spam atau tidak berdasarkan data berlabel. Jenis pembelajaran apa yang digunakan?",
            "options" to listOf(
                "Unsupervised learning",
                "Reinforcement learning",
                "Supervised learning",
                "Deep learning"
            ),
            "correctAnswer" to 2,
            "explanation" to "Disebut pembelajaran dengan pengawasan karena model dilatih menggunakan data yang sudah memiliki label (spam atau tidak). Model belajar dari contoh tersebut untuk mengenali pola dan memprediksi label baru."
        ),
        mapOf(
            "category" to "Machine Learning",
            "question" to "Sebuah model memiliki akurasi tinggi pada data pelatihan, tetapi hasilnya buruk pada data uji. Kondisi ini disebut?",
            "options" to listOf(
                "Underfitting",
                "Overfitting",
                "Regularisasi",
                "Normalisasi"
            ),
            "correctAnswer" to 1,
            "explanation" to "Kondisi ini disebut overfitting karena model terlalu menyesuaikan diri dengan data pelatihan sampai tidak bisa mengenali data baru. Artinya, model hafal data, bukan memahami polanya."
        ),
        mapOf(
            "category" to "Machine Learning",
            "question" to "Dalam kasus deteksi penipuan di mana data sangat tidak seimbang, metrik evaluasi apa yang paling tepat digunakan?",
            "options" to listOf(
                "Akurasi",
                "Presisi",
                "Recall",
                "F1-score"
            ),
            "correctAnswer" to 3,
            "explanation" to "F1-score paling tepat karena menyeimbangkan presisi dan recall. Pada data tidak seimbang, akurasi bisa menipu, sementara F1-score memberi gambaran lebih adil tentang kemampuan model mendeteksi kasus penting."
        )
    )

    val currentQuestion = questions[currentQuestionIndex]
    val totalQuestions = questions.size
    val options = currentQuestion["options"] as List<String>
    val correctAnswer = currentQuestion["correctAnswer"] as Int

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                            selectedAnswer = null
                            showAnswer = false
                        }
                    }
                ) {
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

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF5C6BC0)
                ) {

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF00BCD4),
                trackColor = Color(0xFF3949AB)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card
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
                        text = currentQuestion["category"] as String,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentQuestion["question"] as String,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Options
                    options.forEachIndexed { index, option ->
                        OptionItem(
                            option = option,
                            prefix = ('A' + index).toString(),
                            isSelected = selectedAnswer == index,
                            isCorrect = index == correctAnswer,
                            showResult = showAnswer && answeredQuestions.contains(currentQuestionIndex),
                            onClick = {
                                if (!answeredQuestions.contains(currentQuestionIndex)) {
                                    selectedAnswer = index
                                    showAnswer = false
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show Answer Button
            AnimatedVisibility(visible = selectedAnswer != null && !answeredQuestions.contains(currentQuestionIndex)) {
                TextButton(
                    onClick = {
                        showAnswer = true
                        answeredQuestions = answeredQuestions + currentQuestionIndex
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Show Answers ▼",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Explanation Card
            AnimatedVisibility(visible = showAnswer && answeredQuestions.contains(currentQuestionIndex)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = currentQuestion["explanation"] as String,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            if (currentQuestionIndex == totalQuestions - 1) {
                // Last question - show "Back to home"
                Button(
                    onClick = { /* Navigate to home */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Back to home",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Not last question - show Back and Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (currentQuestionIndex > 0) {
                                currentQuestionIndex--
                                selectedAnswer = null
                                showAnswer = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Back",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (currentQuestionIndex < totalQuestions - 1) {
                                currentQuestionIndex++
                                selectedAnswer = null
                                showAnswer = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    option: String,
    prefix: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showResult && isCorrect -> Color(0xFFE8F5E9)
        showResult && isSelected && !isCorrect -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    val borderColor = when {
        showResult && isCorrect -> Color(0xFF4CAF50)
        showResult && isSelected && !isCorrect -> Color(0xFFF44336)
        isSelected -> Color(0xFF5C6BC0)
        else -> Color(0xFFE0E0E0)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
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
                Text(
                    text = "$prefix. ",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = option,
                    fontSize = 16.sp
                )
            }

            if (showResult) {
                if (isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                } else if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Wrong",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF5C6BC0)
                    )
                )
            }
        }
    }
}

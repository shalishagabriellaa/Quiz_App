package com.example.tubes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizResultScreen(
    scorePercent: Int,
    correctAnswers: Int,
    totalQuestions: Int,
    pointsEarned: Int,
    passingGrade: Int,
    isPassed: Boolean,
    canRetry: Boolean,
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
                modifier = Modifier.padding(28.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (isPassed) "🎉" else "😭", fontSize = 56.sp)

                Spacer(Modifier.height(10.dp))

                Text(
                    text = if (isPassed) "You Passed!" else "You Failed",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )

                Spacer(Modifier.height(8.dp))

                Text("Score: $scorePercent%  •  Passing: $passingGrade%", color = Color.Gray)
                Spacer(Modifier.height(6.dp))
                Text("Correct: $correctAnswers / $totalQuestions", color = Color.Gray)

                Spacer(Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPassed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Points earned", color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "+$pointsEarned",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        if (!isPassed) {
                            Text("Points only granted if you pass.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Back to Home") }

                Spacer(Modifier.height(10.dp))

                if (!isPassed) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Try Again") }

                    Spacer(Modifier.height(10.dp))
                }

                OutlinedButton(
                    onClick = onViewExplanation,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("View Explanation") }
            }
        }
    }
}

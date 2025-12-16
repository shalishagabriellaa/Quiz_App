package com.example.tubes.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.viewmodel.ParticipantSubmission
import com.example.tubes.viewmodel.QuizWithStats
import com.example.tubes.viewmodel.TeacherViewAllUiState

private val DarkNavy = Color(0xFF2E3856)
private val AccentPurple = Color(0xFF7E57C2)
private val AccentPink = Color(0xFFAB47BC)

enum class TeacherViewAllType { AVERAGE_SCORE, RECENT_QUIZ, PARTICIPANTS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherViewAllScreen(
    type: TeacherViewAllType,
    uiState: TeacherViewAllUiState,
    onBack: () -> Unit
) {
    val title = when (type) {
        TeacherViewAllType.AVERAGE_SCORE -> "Average Score per Quiz"
        TeacherViewAllType.RECENT_QUIZ -> "All Recent Quizzes"
        TeacherViewAllType.PARTICIPANTS -> "All Participants"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF2E3856), Color(0xFF3D4A6B)))
                )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(uiState.error ?: "Error", color = Color.Red)
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            when (type) {
                                TeacherViewAllType.AVERAGE_SCORE -> {
                                    if (uiState.quizzes.isEmpty()) item { EmptyState("No quiz found.") }
                                    else items(uiState.quizzes) { AverageScoreCardRapi(it) }
                                }

                                TeacherViewAllType.RECENT_QUIZ -> {
                                    if (uiState.quizzes.isEmpty()) item { EmptyState("No quiz found.") }
                                    else items(uiState.quizzes) { RecentQuizCardRapi(it) }
                                }

                                TeacherViewAllType.PARTICIPANTS -> {
                                    if (uiState.submissions.isEmpty()) item { EmptyState("No participant found.") }
                                    else items(uiState.submissions) { ParticipantCardRapi(it) }
                                }
                            }

                            item { Spacer(Modifier.height(8.dp)) }
                        }
                    }
                }
            }
        }
    }
}

/* ===== Cards ===== */

@Composable
private fun AverageScoreCardRapi(quiz: QuizWithStats) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7FB))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(quiz.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("By ${quiz.authorName}", fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.height(10.dp))

            val progress: Float = (quiz.averageScore / 100.0).coerceIn(0.0, 1.0).toFloat()

            LinearProgressIndicator(
                progress = progress,
                color = AccentPurple,
                trackColor = AccentPink.copy(alpha = 0.18f),
                modifier = Modifier.fillMaxWidth().height(10.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Avg ${quiz.averageScore.toInt()}% • ${quiz.totalParticipants} students • ${quiz.totalQuestions} questions",
                fontSize = 12.sp,
                color = DarkNavy.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun RecentQuizCardRapi(quiz: QuizWithStats) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7FB))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(quiz.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("By ${quiz.authorName}", fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${quiz.totalParticipants} participants • ${quiz.totalQuestions} questions • Avg ${quiz.averageScore.toInt()}%",
                fontSize = 12.sp,
                color = DarkNavy.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun ParticipantCardRapi(sub: ParticipantSubmission) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7FB))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(46.dp).background(AccentPurple.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sub.studentName.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(sub.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text(sub.quizTitle, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF303F9F).copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = sub.score.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF303F9F)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray)
    }
}

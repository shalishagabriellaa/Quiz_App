package com.example.tubes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.data.repository.HomeRepositoryImpl
import java.util.concurrent.TimeUnit
import com.example.tubes.util.formatTimeAgo

/* =========================
 *   DATA CLASSES
 * ========================= */

data class QuizDetail(
    val id: String,
    val title: String,
    val imageUrl: String,
    val participantCount: String,
    val author: QuizAuthor,
    val createdTimeMillis: Long,
    val totalQuestions: Int,
    val questionType: String,
    val duration: String,
    val rules: List<String>
)

data class QuizAuthor(
    val name: String,
    val avatarUrl: String
)

/* =========================
 *   SCREEN (LOAD DARI DB)
 * ========================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestInformationScreen(
    quizId: String,
    onBackClick: () -> Unit = {},
    onStartQuiz: () -> Unit = {}
) {
    val repo = remember { HomeRepositoryImpl() }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var quizDetail by remember { mutableStateOf<QuizDetail?>(null) }

    LaunchedEffect(quizId) {
        try {
            isLoading = true
            error = null

            // ambil quiz dari Firestore
            val quiz = repo.getQuizById(quizId)

            // ambil author
            val user = repo.getUser(quiz.authorId)
            val authorName = user?.fullName ?: user?.name ?: "Unknown"
            val avatarUrl = user?.avatarUrl ?: ""

            // attemptCount -> text peserta
            val participantText = "${quiz.attemptCount} people took this"

            // timer: Long (detik) -> menit (minimal 1 menit)
            val minutes = quiz.timer.let { seconds ->
                val m = TimeUnit.SECONDS.toMinutes(seconds)
                if (m <= 0L) 1L else m
            }

            quizDetail = QuizDetail(
                id = quizId,
                title = quiz.title,
                imageUrl = quiz.bannerUrl,
                participantCount = participantText,
                author = QuizAuthor(
                    name = authorName,
                    avatarUrl = avatarUrl
                ),
                createdTimeMillis = quiz.createdAt?.toDate()?.time ?: 0L,
                totalQuestions = quiz.questionCount.toInt(),
                questionType = "Multiple Choice Question",
                duration = "$minutes mins",
                rules = listOf(
                    "You must complete this test in one session – make sure your internet is reliable.",
                    "1 mark awarded for a correct answer. No negative marking will be there for wrong answer.",
                    "More you give the correct answer more chance to win the badge.",
                    "If you don't earn a badge this time, you can retake this test once more."
                )
            )
        } catch (e: Exception) {
            error = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEDE7F6)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEDE7F6)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $error", color = Color.Red)
            }
        }

        quizDetail != null -> {
            TestInformationContent(
                quizDetail = quizDetail!!,
                onBackClick = onBackClick,
                onStartQuiz = onStartQuiz
            )
        }
    }
}

/* =========================
 *   UI CONTENT
 * ========================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestInformationContent(
    quizDetail: QuizDetail,
    onBackClick: () -> Unit,
    onStartQuiz: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = quizDetail.title,
                        color = Color(0xFF1A1A1A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        modifier = Modifier.padding(start = 8.dp)   // ★ JARAK TITLE DARI ICON
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.08f)) // sama style dengan Category, adaptif
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black // adaptif ke background terang
                        )
                    }
                },
                        colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEDE7F6)
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E4FA0)
                )
            ) {
                Text(
                    text = "Start Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDE7F6))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuizImageCard(
                imageUrl = quizDetail.imageUrl,
                participantCount = quizDetail.participantCount
            )

            AuthorInfoSection(
                author = quizDetail.author,
                createdTimeMillis = quizDetail.createdTimeMillis
            )

            QuizStatsSection(
                totalQuestions = quizDetail.totalQuestions,
                questionType = quizDetail.questionType,
                duration = quizDetail.duration
            )

            RulesSection(rules = quizDetail.rules)

            Text(
                text = "ALL THE BEST!!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun QuizImageCard(
    imageUrl: String,
    participantCount: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Quiz Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF9C7FB5))
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = participantCount,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun AuthorInfoSection(
    author: QuizAuthor,
    createdTimeMillis: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LEFT: Avatar + Name
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            ) {
                if (author.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = author.avatarUrl,
                        contentDescription = author.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = author.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }

        // RIGHT: Time Ago
        Text(
            text = formatTimeAgo(createdTimeMillis),
            fontSize = 13.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
fun QuizStatsSection(
    totalQuestions: Int,
    questionType: String,
    duration: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Questions",
                tint = Color(0xFF5E4FA0),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = totalQuestions.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = questionType,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Duration",
                tint = Color(0xFF5E4FA0),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = duration,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "All Questions",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun RulesSection(rules: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Before you start",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            rules.forEach { rule ->
                RuleItem(rule = rule)
            }
        }
    }
}

@Composable
fun RuleItem(rule: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "•",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = rule,
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            lineHeight = 20.sp
        )
    }
}

package com.example.tubes.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tubes.viewmodel.AuthorStats
import com.example.tubes.viewmodel.AuthorViewModel
import com.example.tubes.viewmodel.ParticipantSubmission
import com.example.tubes.viewmodel.QuizWithStats

// Color Palette
private val DarkNavy = Color(0xFF2E3856)
private val LightPurple = Color(0xFFE8E4F3)
private val AccentPurple = Color(0xFF7E57C2)
private val AccentPink = Color(0xFFAB47BC)
private val LightPink = Color(0xFFCE93D8)
private val CardBg = Color.White

@Composable
fun TeacherDashboard(
    authorId: String?,
    onOpenNotifications: () -> Unit,

    // ✅ tambahan aman (default kosong)
    onSearchSubmit: (String) -> Unit = {},
    onViewAllAverage: () -> Unit = {},
    onViewAllRecent: () -> Unit = {},
    onViewAllParticipants: () -> Unit = {},

    authorViewModel: AuthorViewModel = viewModel()
) {
    val uiState by authorViewModel.uiState.collectAsState()

    LaunchedEffect(authorId) {
        if (!authorId.isNullOrEmpty()) {
            authorViewModel.loadDashboardData(authorId)
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3856),
                        Color(0xFF3D4A6B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            HeaderSection(
                authorName = uiState.authorName,
                unreadCount = uiState.unreadNotificationCount,
                onNotificationClick = onOpenNotifications
            )

//            SearchBarSection(
//                onSubmit = onSearchSubmit
//            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
                ) {
                    Text(
                        text = "⚠️ ${uiState.error}",
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                StatsSection(uiState.overallStats)

                AverageScoreSection(
                    quizzes = uiState.averageScoresPerQuiz.take(3),
                    onViewAll = onViewAllAverage
                )

                RecentQuizSection(
                    recentQuizzes = uiState.recentQuizzes.take(3),
                    onViewAll = onViewAllRecent
                )

                ParticipantInfoSection(
                    submissions = uiState.recentSubmissions.take(3),
                    onViewAll = onViewAllParticipants
                )

            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HeaderSection(
    authorName: String,
    unreadCount: Int,
    onNotificationClick: () -> Unit
) {
    // ✅ settings & profile dihapus, notif doang
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Welcome,",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Text(
                authorName.ifEmpty { "Selena" },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = DarkNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFFFF5252), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        unreadCount.coerceAtMost(9).toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

//@Composable
//fun SearchBarSection(
//    onSubmit: (String) -> Unit
//) {
//    var query by remember { mutableStateOf("") }
//
//    OutlinedTextField(
//        value = query,
//        onValueChange = { query = it },
//        placeholder = {
//            Text(
//                "Search your quiz...",
//                color = Color(0xFF9E9E9E),
//                fontSize = 14.sp
//            )
//        },
//        leadingIcon = {
//            Icon(
//                imageVector = Icons.Default.Search,
//                contentDescription = "Search",
//                tint = Color(0xFF9E9E9E)
//            )
//        },
//        trailingIcon = {
//            IconButton(onClick = { onSubmit(query.trim()) }) {
//                Icon(
//                    imageVector = Icons.Default.ArrowForward,
//                    contentDescription = "Submit",
//                    tint = AccentPurple
//                )
//            }
//        },
//        singleLine = true,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp, vertical = 8.dp)
//            .shadow(4.dp, RoundedCornerShape(28.dp))
//            .onKeyEvent { keyEvent ->
//                if (
//                    keyEvent.type == KeyEventType.KeyUp &&
//                    keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER
//                ) {
//                    onSubmit(query.trim())
//                    true
//                } else {
//                    false
//                }
//            },
//        shape = RoundedCornerShape(28.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            unfocusedContainerColor = LightPurple,
//            focusedContainerColor = Color.White,
//            unfocusedBorderColor = Color.Transparent,
//            focusedBorderColor = AccentPurple
//        )
//    )
//}

@Composable
fun StatsSection(stats: AuthorStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItemModern(
                    icon = Icons.Default.CheckCircle,
                    iconBg = Color(0xFFE1BEE7),
                    iconColor = Color(0xFF9C27B0),
                    label = "Total Kuis",
                    value = stats.totalQuizzes.toString(),
                    valueColor = Color(0xFF9C27B0)
                )
                StatItemModern(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFFBBDEFB),
                    iconColor = Color(0xFF1976D2),
                    label = "Total Peserta",
                    value = stats.totalParticipants.toString(),
                    valueColor = Color(0xFF1565C0)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItemModern(
                    icon = Icons.Default.AccountCircle,
                    iconBg = Color(0xFFC5CAE9),
                    iconColor = Color(0xFF5E35B1),
                    label = "Followers",
                    value = "1.2K",
                    valueColor = Color(0xFF512DA8)
                )
                StatItemModern(
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFFFFF9C4),
                    iconColor = Color(0xFFFFA000),
                    label = "Rata-rata Skor",
                    value = "%.1f%%".format(stats.averageQuizScore),
                    valueColor = Color(0xFFFF8F00)
                )
            }
        }
    }
}

@Composable
fun StatItemModern(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color
) {
    Column(modifier = Modifier.width(155.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .background(iconBg, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
    }
}

@Composable
fun AverageScoreSection(
    quizzes: List<QuizWithStats>,
    onViewAll: () -> Unit
) {
    if (quizzes.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Average Score per Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${quizzes.sumOf { it.totalParticipants }} Students",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            TextButton(onClick = onViewAll) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "View all",
                        color = LightPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = LightPink,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        quizzes.forEach { quiz ->
            ScoreBarItem(quiz.title, quiz.averageScore.toInt())
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ScoreBarItem(subject: String, avgScore: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subject,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .background(LightPink.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AVG $avgScore",
                        color = LightPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(avgScore / 100f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AccentPurple, AccentPink)
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                Text("100", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun RecentQuizSection(
    recentQuizzes: List<QuizWithStats>,
    onViewAll: () -> Unit
) {
    if (recentQuizzes.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Quiz",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = onViewAll) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "View all",
                        color = LightPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = LightPink,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        recentQuizzes.forEach { quiz ->
            RecentQuizCard(quiz)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun RecentQuizCard(quiz: QuizWithStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFB3BA),
                                Color(0xFFFFACC1)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        "${quiz.totalQuestions}Qs",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quiz.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF212121),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        quiz.authorName,
                        color = Color(0xFF757575),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuizBadgeModern(
                        "${quiz.totalParticipants} Participants",
                        AccentPink
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Avg Score ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                "${quiz.averageScore.toInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizBadgeModern(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ParticipantInfoSection(
    submissions: List<ParticipantSubmission>,
    onViewAll: () -> Unit
) {
    if (submissions.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Participant Info",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
//            TextButton(onClick = onViewAll) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        "View all",
//                        color = LightPink,
//                        fontSize = 13.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                    Icon(
//                        imageVector = Icons.Default.ArrowForward,
//                        contentDescription = null,
//                        tint = LightPink,
//                        modifier = Modifier.size(16.dp)
//                    )
//                }
//            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                submissions.forEachIndexed { index, sub ->
                    ParticipantRow(sub)
                    if (index < submissions.lastIndex) {
                        Divider(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantRow(submission: ParticipantSubmission) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(4.dp, CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7E57C2),
                            Color(0xFFAB47BC)
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                submission.studentName.first().uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                submission.studentName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Completed",
                color = Color(0xFF4CAF50),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                submission.quizTitle,
                color = Color(0xFF757575),
                fontSize = 11.sp,
                maxLines = 1
            )
        }
        Box(
            modifier = Modifier
                .background(
                    Color(0xFF303F9F).copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                "${submission.score}",
                color = Color(0xFF303F9F),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
        }
    }
}

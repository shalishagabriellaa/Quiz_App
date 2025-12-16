package com.example.tubes.ui.teacher

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun TeacherDashboard(
    authorId: String?,
    onOpenNotifications: () -> Unit,
    authorViewModel: AuthorViewModel = viewModel()
) {
    val uiState by authorViewModel.uiState.collectAsState()

    LaunchedEffect(authorId) {
        if (!authorId.isNullOrEmpty()) {
            authorViewModel.loadDashboardData(authorId)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFF2E3856))
            .verticalScroll(scrollState)
    ) {
        HeaderSection(
            authorName = uiState.authorName,
            unreadCount = uiState.unreadNotificationCount, // nanti dari ViewModel
            onNotificationClick = onOpenNotifications
        )

        SearchBarSection()

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
            Text(
                text = "Gagal memuat data: ${uiState.error}",
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            StatsSection(uiState.overallStats)
            AverageScoreSection(uiState.averageScoresPerQuiz)
            RecentQuizSection(uiState.recentQuizzes)
            ParticipantInfoSection(uiState.recentSubmissions)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

    @Composable
    fun HeaderSection(
        authorName: String,
        unreadCount: Int,
        onNotificationClick: () -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Welcome $authorName", color = Color.White)

            Box {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF3D4A6B), CircleShape)
                        .padding(8.dp)
                        .clickable(onClick = onNotificationClick)
                )

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }


// Search bar tidak diubah
@Composable
fun SearchBarSection() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search your quiz...", color = Color(0xFF9E9E9E), fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFE8E4F3),
            focusedContainerColor = Color(0xFFE8E4F3),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun StatsSection(stats: AuthorStats) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItemCompact(
                    icon = Icons.Default.CheckCircle,
                    iconBg = Color(0xFFE1BEE7),
                    iconColor = Color(0xFF9C27B0),
                    label = "Total Kuis",
                    value = stats.totalQuizzes.toString(), // DATA DINAMIS
                    valueColor = Color(0xFF9C27B0)
                )
                StatItemCompact(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFFD1C4E9),
                    iconColor = Color(0xFF673AB7),
                    label = "Total Peserta",
                    value = stats.totalParticipants.toString(), // DATA DINAMIS
                    valueColor = Color(0xFF5E35B1)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItemCompact(
                    icon = Icons.Default.AccountCircle,
                    iconBg = Color(0xFFD1C4E9),
                    iconColor = Color(0xFF5E35B1),
                    label = "Followers",
                    value = "N/A", // Data ini tidak kita ambil
                    valueColor = Color(0xFF512DA8)
                )
                StatItemCompact(
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFFE1BEE7),
                    iconColor = Color(0xFF9C27B0),
                    label = "Rata-rata Skor",
                    // Format skor menjadi persentase
                    value = "%.1f%%".format(stats.averageQuizScore), // DATA DINAMIS
                    valueColor = Color(0xFFAB47BC)
                )
            }
        }
    }
}

@Composable
fun StatItemCompact(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(155.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF757575),
                fontWeight = FontWeight.Normal
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
fun AverageScoreSection(quizzes: List<QuizWithStats>) { // Tambah parameter
    if (quizzes.isEmpty()) return // Jangan tampilkan jika list kosong

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Average Score per Quiz",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    // Anda bisa buat ini dinamis jika perlu
                    text = "${quizzes.sumOf { it.totalParticipants }} Students engaged",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5)
                )
            }
            TextButton(onClick = {}) {
                Text(
                    "View all →",
                    color = Color(0xFF7986CB),
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Loop untuk menampilkan data dinamis
        quizzes.forEach { quiz ->
            ScoreBarItem(quiz.title, quiz.averageScore.toInt())
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun ScoreBarItem(subject: String, avgScore: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subject,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "AVG $avgScore",
                color = Color(0xFFCE93D8),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFF3D4A6B), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(avgScore / 100f)
                    .height(6.dp)
                    .background(Color(0xFF5E35B1), RoundedCornerShape(3.dp))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0", color = Color(0xFF90A4AE), fontSize = 10.sp)
            Text("100", color = Color(0xFF90A4AE), fontSize = 10.sp)
        }
    }
}

@Composable
fun RecentQuizSection(recentQuizzes: List<QuizWithStats>) {
    if (recentQuizzes.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Quiz",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = {}) {
                Text(
                    "View all →",
                    color = Color(0xFF7986CB),
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Loop untuk menampilkan data dinamis
        recentQuizzes.forEach { quiz ->
            RecentQuizCard(quiz) // Gunakan composable baru
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// BUAT FUNGSI BARU UNTUK CARD-NYA AGAR LEBIH RAPI
@Composable
fun RecentQuizCard(quiz: QuizWithStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFB3BA)), // Bisa dibuat dinamis juga
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quiz.title, // Dinamis
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF212121)
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
                        quiz.authorName, // Dinamis
                        color = Color(0xFF757575),
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    QuizBadge("${quiz.totalQuestions} Qs", Color(0xFF7E57C2))
                    Spacer(modifier = Modifier.width(6.dp))
                    QuizBadge("${quiz.totalParticipants} Participants", Color(0xFFAB47BC))
                }
            }
        }
    }
}

// Tambahkan juga QuizBadge Composable jika belum ada
@Composable
fun QuizBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun ParticipantInfoSection(submissions: List<ParticipantSubmission>) {
    if (submissions.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Participant Info",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                submissions.forEachIndexed { index, sub ->
                    ParticipantRow(sub)
                    if (index < submissions.lastIndex) {
                        Divider(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// Fungsi untuk menampilkan satu baris participant
@Composable
fun ParticipantRow(submission: ParticipantSubmission) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Participant",
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(submission.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Mengerjakan '${submission.quizTitle}'", color = Color.Gray, fontSize = 11.sp)
        }
        Text(
            "${submission.score}",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
    }
}


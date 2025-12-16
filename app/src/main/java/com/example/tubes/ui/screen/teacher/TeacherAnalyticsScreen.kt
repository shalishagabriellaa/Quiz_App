package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.data.model.TeacherQuizAnalytics
import com.example.tubes.data.model.TeacherQuizAnalyticsSummary
import com.example.tubes.viewmodel.TeacherAnalyticsViewModel

// ===== Palette (seragam dengan screen lain) =====
private val TopBarColor = Color(0xFF252A57)
private val PageBgTop = Color(0xFFF6F2FF)
private val PageBgBottom = Color(0xFFF2F4FF)
private val CardStroke = Color(0xFFE9EAF5)
private val MutedText = Color(0xFF7A7F9A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAnalyticsScreen(
    viewModel: TeacherAnalyticsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Monitoring",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PageBgTop, PageBgBottom)))
                .padding(padding)
        ) {
            if (state.isLoading) {
                LoadingScreen()
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Track quiz performance and participation trends",
                    color = MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                // SECTION 1: Bar chart (dibungkus card biar rapi)
                SectionCard(
                    title = "Participants per Quiz",
                    subtitle = "Top quizzes by participants",
                    iconTint = Color(0xFF3E4396)
                ) {
                    QuizParticipantBarChart(data = state.barChartData)
                }

                Spacer(Modifier.height(12.dp))

                // SECTION 2: Summary cards (dibikin 2 kartu seperti referensi)
                state.summary?.let { summary ->
                    AnalyticsSummaryCards(summary = summary)
                    Spacer(Modifier.height(6.dp))
                }

                Spacer(Modifier.height(8.dp))

                // SECTION 3: List
                SectionCard(
                    title = "Top Quizzes",
                    subtitle = "Sorted by participants",
                    iconTint = Color(0xFF3E4396)
                ) {
                    QuizAnalyticsList(quizzes = state.quizList)
                }

                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

/* =========================
   SECTION WRAPPER
   ========================= */

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    iconTint: Color = Color(0xFF3E4396),
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14162B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}

/* =========================
   SECTION 3 - LIST
   ========================= */

@Composable
fun QuizAnalyticsList(
    quizzes: List<TeacherQuizAnalytics>
) {
    if (quizzes.isEmpty()) {
        Text("No analytics data", color = MutedText, fontSize = 12.sp)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        quizzes.forEach { quiz ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = quiz.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF14162B),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatPill(
                            icon = Icons.Default.Groups,
                            label = "Participants",
                            value = quiz.totalParticipants.toString(),
                            tint = Color(0xFF3E4396)
                        )
                        StatPill(
                            icon = Icons.Default.Star,
                            label = "Avg Score",
                            value = String.format("%.1f", quiz.averageScore),
                            tint = Color(0xFF6B58E9)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(tint.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$label $value",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* =========================
   SECTION 2 - SUMMARY CARDS
   ========================= */

@Composable
fun AnalyticsSummaryCards(
    summary: TeacherQuizAnalyticsSummary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCardModern(
            title = "Average Score",
            value = String.format("%.1f", summary.globalAverageScore),
            tint = Color(0xFF6B58E9),
            modifier = Modifier.weight(1f)
        )
        SummaryCardModern(
            title = "Total Participants",
            value = summary.globalParticipants.toString(),
            tint = Color(0xFF3E4396),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCardModern(
    title: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* =========================
   SECTION 1 - BAR CHART
   (logika sama, cuma UI dibagusin)
   ========================= */

@Composable
fun QuizParticipantBarChart(
    data: List<TeacherQuizAnalytics>
) {
    if (data.isEmpty()) {
        Text("No analytics data", color = MutedText, fontSize = 12.sp)
        return
    }

    val max = data.maxOf { it.totalParticipants }.coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        data.forEach { quiz ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quiz.title,
                        fontSize = 12.sp,
                        color = Color(0xFF14162B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${quiz.totalParticipants}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3E4396)
                    )
                }

                Spacer(Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE9EAF5))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(quiz.totalParticipants / max.toFloat())
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFF3E4396))
                    )
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "participants",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }
    }
}

/* =========================
   LOADING
   ========================= */

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

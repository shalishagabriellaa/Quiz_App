package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.data.model.RecentQuizActivity
import com.example.tubes.viewmodel.TeacherProfileViewModel
import kotlin.math.sin
import kotlin.math.PI


@Composable
fun TeacherProfileScreen(
    viewModel: TeacherProfileViewModel
) {
    val state by viewModel.uiState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        ProfileHeader(
            fullName = state.fullName,
            avatarUrl = state.avatarUrl
        )

        state.stats?.let {
            ProfileStatsSection(
                totalQuizzes = it.totalQuizzes,
                totalParticipants = it.totalParticipants
            )
        }

        RecentActivitiesSection(
            quizzes = state.recentQuizzes
        )

        ExtraSection(
            weeklyQuizCount = state.weeklyQuizCount
        )
    }
}

@Composable
fun ProfileHeader(
    fullName: String,
    avatarUrl: String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(colorScheme.surfaceVariant)
        )

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Educator",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.primary
            )
        }
    }
}
@Composable
fun ProfileStatsSection(
    totalQuizzes: Int,
    totalParticipants: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileStatCard(
            title = "Total Quizzes",
            value = totalQuizzes.toString(),
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            title = "Participants",
            value = totalParticipants.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
fun RecentActivitiesSection(
    quizzes: List<RecentQuizActivity>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Recent Activities",
            style = MaterialTheme.typography.titleMedium
        )

        if (quizzes.isEmpty()) {
            Text(
                text = "No recent activities",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            return
        }

        quizzes.forEach { quiz ->
            RecentQuizCard(quiz)
        }
    }
}
@Composable
fun RecentQuizCard(
    quiz: RecentQuizActivity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = quiz.bannerUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.surfaceVariant)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${quiz.totalParticipants} participants",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
@Composable
fun ExtraSection(
    weeklyQuizCount: List<Int>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Quiz Creation (Last 7 Days)",
            style = MaterialTheme.typography.titleMedium
        )

        WeeklyQuizBarChart(
            data = weeklyQuizCount
        )
    }
}

@Composable
fun WeeklyQuizBarChart(
    data: List<Int>
) {
    if (data.isEmpty()) {
        Text(
            text = "No quiz activity",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        return
    }

    val max = (data.maxOrNull() ?: 1).coerceAtLeast(1)

    // Fix: Read the color from the theme here, outside the Canvas lambda.
    val barColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val barWidth = size.width / (data.size * 2)

        data.forEachIndexed { index, value ->
            val barHeight =
                (value / max.toFloat()) * size.height

            drawRect(
                // Use the variable that holds the color.
                color = barColor,
                topLeft = Offset(
                    x = index * barWidth * 2 + barWidth / 2,
                    y = size.height - barHeight
                ),
                size = Size(
                    width = barWidth,
                    height = barHeight
                )
            )
        }
    }

    // LABEL BAWAH
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("D-6", "D-5", "D-4", "D-3", "D-2", "D-1", "Today")
            .forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
    }
}

@Composable
fun ProfileStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(96.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


package com.example.tubes.ui.screen.teacher

import android.R.attr.description
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tubes.data.model.TeacherQuizAnalytics
import com.example.tubes.data.model.TeacherQuizAnalyticsSummary
import com.example.tubes.viewmodel.TeacherAnalyticsViewModel

@Composable
fun TeacherAnalyticsScreen(
    viewModel: TeacherAnalyticsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    Column {

        // SECTION 1
        QuizParticipantBarChart(
            data = state.barChartData
        )

        // SECTION 2
        state.summary?.let {
            AnalyticsSummaryCards(summary = it)
        }

        // SECTION 3
        QuizAnalyticsList(
            quizzes = state.quizList
        )
    }
}

@Composable
fun QuizAnalyticsList(
    quizzes: List<TeacherQuizAnalytics>
) {
    Column {
        Text(
            text = "Top Quizzes by Participants",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        quizzes.forEach { quiz ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text(
                        text = quiz.title,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Average Score: ${quiz.averageScore}"
                    )

                    Text(
                        text = "Participants: ${quiz.totalParticipants}"
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsSummaryCards(
    summary: TeacherQuizAnalyticsSummary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SummaryCard(
            title = "Average Score",
            value = String.format("%.1f", summary.globalAverageScore)
        )

        SummaryCard(
            title = "Total Participants",
            value = summary.globalParticipants.toString()
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
@Composable
fun QuizParticipantBarChart(
    data: List<TeacherQuizAnalytics>
) {
    if (data.isEmpty()) {
        Text("No analytics data")
        return
    }

    val max = data.maxOf { it.totalParticipants }.coerceAtLeast(1)

    Column {

        Text(
            text = "Participants per Quiz",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        data.forEach { quiz ->

            Column(
                modifier = Modifier.padding(vertical = 6.dp)
            ) {

                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.bodyMedium
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(
                                quiz.totalParticipants / max.toFloat()
                            )
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Text(
                    text = "${quiz.totalParticipants} participants",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}



@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAnalysisScreen() {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quiz Analysis",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8DEF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E2847)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Quiz Statistics Section
            Text(
                "Quiz Statistics",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "12 Quizzes",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bar Chart
            BarChart()

            Spacer(modifier = Modifier.height(24.dp))

            // Quiz Performance Section
            Text(
                "Quiz Performance",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PerformanceCard("Average Score", "75%", Color(0xFF4A3D8F))
                PerformanceCard("Highest Score", "95%", Color(0xFF4A3D8F))
                PerformanceCard("Completed", "85%", Color(0xFF4A3D8F))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Results Summary Section
            Text(
                "Results Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ParticipantItem("Participant A", "80%", Color(0xFF6750A4))
            Spacer(modifier = Modifier.height(12.dp))
            ParticipantItem("Participant B", "80%", Color(0xFF6750A4))
            Spacer(modifier = Modifier.height(12.dp))
            ParticipantItem("Participant C", "70%", Color(0xFF6750A4))

            Spacer(modifier = Modifier.height(24.dp))

            // Questions Section
            QuestionItem("Question 1", "Avg. Score: 75%", Color(0xFFFF5252))
            Spacer(modifier = Modifier.height(12.dp))
            QuestionItem("Question 2", "Avg. Score: 80%", Color(0xFFFF5252))
        }
    }
}

@Composable
fun BarChart() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        val barHeights = listOf(0.6f, 0.4f, 0.3f, 0.55f, 0.5f, 0.8f, 0.65f)
        val barColors = listOf(
            Color(0xFF4A3D8F),
            Color(0xFF4A3D8F),
            Color(0xFF2C2C2C),
            Color(0xFF4A3D8F),
            Color(0xFF2C2C2C),
            Color(0xFF2C1F6F),
            Color(0xFF6750A4)
        )

        barHeights.forEachIndexed { index, height ->
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight(height)
                    .background(barColors[index], RoundedCornerShape(4.dp))
            )
        }
    }

    Text(
        "Metrics",
        fontSize = 10.sp,
        color = Color(0xFF6750A4),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, end = 16.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.End
    )
}

@Composable
fun PerformanceCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 13.sp
            )
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ParticipantItem(name: String, score: String, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8DEF8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                name,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Text(
            "Score: $score",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun QuestionItem(title: String, avgScore: String, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            Text(
                title,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Text(
            avgScore,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

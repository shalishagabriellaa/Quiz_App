package com.example.tubes.ui.teacher

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TeacherDashboard() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3856))
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // Header
        HeaderSection()

        // Search Bar
        SearchBarSection()

        // Stats Cards
        StatsSection()

        // Average Score Section
        AverageScoreSection()

        // Recent Quiz Section
        RecentQuizSection()

        // Participant Info Section
        ParticipantInfoSection()
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF3D4A6B), CircleShape)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Welcome, Selena!",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF3D4A6B), CircleShape)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8A80))
            )
        }
    }
}

@Composable
fun SearchBarSection() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = {
            Text(
                "Search your quiz...",
                color = Color(0xFF9E9E9E),
                fontSize = 14.sp
            )
        },
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
fun StatsSection() {
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
                    value = "12",
                    valueColor = Color(0xFF9C27B0)
                )
                StatItemCompact(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFFD1C4E9),
                    iconColor = Color(0xFF673AB7),
                    label = "Total Peserta",
                    value = "452",
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
                    value = "1.2K",
                    valueColor = Color(0xFF512DA8)
                )
                StatItemCompact(
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFFE1BEE7),
                    iconColor = Color(0xFF9C27B0),
                    label = "Rata-rata Skor",
                    value = "78.4%",
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
fun AverageScoreSection() {
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
                    text = "100 Students",
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

        ScoreBarItem("Basic Algebra", 78)
        Spacer(modifier = Modifier.height(16.dp))
        ScoreBarItem("Tenses", 86)
        Spacer(modifier = Modifier.height(16.dp))
        ScoreBarItem("Trigonometry", 65)
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
fun RecentQuizSection() {
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
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFB3BA)),
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

                // Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Machine Learning Practice Test",
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
                            "Won Guntur Alam",
                            color = Color(0xFF757575),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        QuizBadge("3 Qs", Color(0xFF7E57C2))
                        Spacer(modifier = Modifier.width(6.dp))
                        QuizBadge("120 Participants", Color(0xFFAB47BC))
                        Spacer(modifier = Modifier.width(6.dp))
                        QuizBadge("Avg Score 81", Color(0xFF8E24AA))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Buttons
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF5E35B1)
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Detail", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizBadge(text: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ParticipantInfoSection() {
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
                text = "Participant Info",
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

        ParticipantCard(
            name = "Kevin Santosa",
            quizName = "Machine Learning",
            score = 96
        )
        Spacer(modifier = Modifier.height(12.dp))
        ParticipantCard(
            name = "Kevin Santosa",
            quizName = "English Grammar - Tenses",
            score = 96
        )
    }
}

@Composable
fun ParticipantCard(name: String, quizName: String, score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5C6BC0))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = quizName,
                    color = Color(0xFF757575),
                    fontSize = 12.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Completed",
                    color = Color(0xFF66BB6A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "2025-11-01",
                    color = Color(0xFF9E9E9E),
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFF5E35B1), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

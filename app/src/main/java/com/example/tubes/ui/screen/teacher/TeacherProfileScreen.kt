package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.data.model.RecentQuizActivity
import com.example.tubes.viewmodel.TeacherProfileViewModel

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherProfileScreen(
    viewModel: TeacherProfileViewModel,
    onPersonalInfo: () -> Unit,
    onChangePassword: () -> Unit,
    onAboutQuorri: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFF44336)
                )
            },
            title = { Text("Logout Confirmation", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out from your account?", fontSize = 14.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = LightBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ===== 1) HEADER PROFILE =====
            ProfileHeaderCard(
                fullName = state.fullName,
                avatarUrl = state.avatarUrl
            )

            // ===== 2) STATS =====
            state.stats?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCardModern(
                        title = "Total Quizzes",
                        value = it.totalQuizzes.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCardModern(
                        title = "Participants",
                        value = it.totalParticipants.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ===== 3) RECENT ACTIVITIES =====
            RecentActivitiesCard(quizzes = state.recentQuizzes)

            // ===== 4) WEEKLY CHART =====
            WeeklyChartCard(weeklyQuizCount = state.weeklyQuizCount)

            // ===== 5) ACCOUNT MENU =====
            Text(
                text = "Account",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14162B),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            SettingMenuItem(
                icon = Icons.Filled.Person,
                title = "Personal Info",
                backgroundColor = Color(0xFFFBDCA0),
                iconTint = Color(0xFFFF9800),
                onClick = onPersonalInfo
            )

            SettingMenuItem(
                icon = Icons.Filled.Lock,
                title = "Change Password",
                backgroundColor = Color(0xFFCFF6D5),
                iconTint = Color(0xFF4CAF50),
                onClick = onChangePassword
            )

            SettingMenuItem(
                icon = Icons.Filled.Info,
                title = "About Quorri",
                backgroundColor = Color(0xFFF5F2A7),
                iconTint = Color(0xFFFBC02D),
                onClick = onAboutQuorri
            )

            SettingMenuItem(
                icon = Icons.Filled.Logout,
                title = "Logout",
                backgroundColor = Color(0xFFF8B5B5),
                iconTint = Color(0xFFEF5350),
                textColor = Color(0xFFEF5350),
                onClick = { showLogoutDialog = true }
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    fullName: String,
    avatarUrl: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = avatarUrl ?: "",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant)
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName.ifBlank { "Teacher" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF14162B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Educator",
                    fontSize = 13.sp,
                    color = Color(0xFF4C54B5),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProfileStatCardModern(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF7A7F9A),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14162B)
            )
        }
    }
}

@Composable
private fun RecentActivitiesCard(
    quizzes: List<RecentQuizActivity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Recent Activities",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14162B),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(10.dp))

            if (quizzes.isEmpty()) {
                Text(
                    text = "No recent activities",
                    fontSize = 13.sp,
                    color = Color(0xFF7A7F9A)
                )
                return@Column
            }

            quizzes.forEachIndexed { idx, quiz ->
                if (idx > 0) Spacer(Modifier.height(10.dp))
                RecentQuizRow(quiz = quiz)
            }
        }
    }
}

@Composable
private fun RecentQuizRow(
    quiz: RecentQuizActivity
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = quiz.bannerUrl,
            contentDescription = null,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surfaceVariant)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quiz.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF14162B)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${quiz.totalParticipants} participants",
                fontSize = 12.sp,
                color = Color(0xFF7A7F9A)
            )
        }
    }
}

@Composable
private fun WeeklyChartCard(
    weeklyQuizCount: List<Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Quiz Creation (Last 7 Days)",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14162B),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(10.dp))

            WeeklyQuizBarChart(data = weeklyQuizCount)
        }
    }
}

@Composable
private fun WeeklyQuizBarChart(
    data: List<Int>
) {
    if (data.isEmpty()) {
        Text(
            text = "No quiz activity",
            fontSize = 13.sp,
            color = Color(0xFF7A7F9A)
        )
        return
    }

    val max = (data.maxOrNull() ?: 1).coerceAtLeast(1)
    val barColor = Color(0xFF4C54B5)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val barWidth = size.width / (data.size * 2f)

        data.forEachIndexed { index, value ->
            val barHeight = (value / max.toFloat()) * size.height
            drawRect(
                color = barColor,
                topLeft = Offset(
                    x = index * barWidth * 2f + barWidth / 2f,
                    y = size.height - barHeight
                ),
                size = Size(width = barWidth, height = barHeight)
            )
        }
    }

    Spacer(Modifier.height(6.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("D-6", "D-5", "D-4", "D-3", "D-2", "D-1", "Today").forEach {
            Text(text = it, fontSize = 11.sp, color = Color(0xFF7A7F9A))
        }
    }
}

@Composable
private fun SettingMenuItem(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    iconTint: Color,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

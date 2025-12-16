package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.data.model.TeacherNotificationUi
import com.example.tubes.viewmodel.TeacherNotificationViewModel

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNotificationScreen(
    viewModel: TeacherNotificationViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // ✅ tetap jalan: auto mark all read
    LaunchedEffect(Unit) {
        viewModel.markAllAsRead()
    }

    Scaffold(
        containerColor = LightBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { padding ->

        val list = state.notifications

        if (list.isEmpty()) {
            EmptyNotificationState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LightBackground),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { notif ->
                TeacherNotificationCard(notification = notif)
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun EmptyNotificationState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(LightBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = Color(0xFF7A7F9A),
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "No notifications yet",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14162B),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "We'll notify you when something happens.",
                color = Color(0xFF7A7F9A),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun TeacherNotificationCard(
    notification: TeacherNotificationUi
) {
    val meta = remember(notification.type) { notifMeta(notification.type) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF1F3FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(meta.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta.icon,
                    contentDescription = null,
                    tint = meta.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color(0xFF14162B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // ✅ dot indikator unread biar clean
                    if (!notification.isRead) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4C54B5))
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5F6786),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                // Kalau di model kamu ada time/timestamp string, kamu bisa tampilkan di sini.
                // Contoh: notification.timeText / createdAtText / etc.
                // Kalau tidak ada fieldnya, biarkan aja (tidak akan error).
            }
        }
    }
}

private data class NotifMeta(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: Color,
    val iconBg: Color
)

private fun notifMeta(type: String): NotifMeta {
    return when (type) {
        "QUIZ_CREATED" -> NotifMeta(
            icon = Icons.Default.AddCircle,
            iconTint = Color(0xFF4CAF50),
            iconBg = Color(0xFFE8F5E9)
        )

        "QUIZ_DELETED" -> NotifMeta(
            icon = Icons.Default.Delete,
            iconTint = Color(0xFFEF5350),
            iconBg = Color(0xFFFFEBEE)
        )

        "QUIZ_COMPLETED" -> NotifMeta(
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFF1E88E5),
            iconBg = Color(0xFFE3F2FD)
        )

        else -> NotifMeta(
            icon = Icons.Default.Notifications,
            iconTint = Color(0xFF7A7F9A),
            iconBg = Color(0xFFF1F3F6)
        )
    }
}

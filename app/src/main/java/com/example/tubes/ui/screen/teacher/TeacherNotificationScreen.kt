package com.example.tubes.ui.screen.teacher


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.ui.theme.Purple40
import androidx.compose.material.ripple.rememberRipple
import com.example.tubes.data.model.TeacherNotificationUi
import com.example.tubes.viewmodel.TeacherNotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNotificationScreen(
    viewModel: TeacherNotificationViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.markAllAsRead()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.notifications, key = { it.id }) { notif ->
                TeacherNotificationCard(notif)
            }
        }
    }
}


@Composable
fun TeacherNotificationCard(
    notification: TeacherNotificationUi
) {
    val (icon, tint, bg) = when (notification.type) {
        "QUIZ_CREATED" ->
            Triple(Icons.Default.Add, Color(0xFF5B7C99), Color(0xFFE8EEF5))
        "QUIZ_DELETED" ->
            Triple(Icons.Default.Delete, Color(0xFFE57373), Color(0xFFFFEBEE))
        "QUIZ_COMPLETED" ->
            Triple(Icons.Default.CheckCircle, Color(0xFF5B7C99), Color(0xFFE8EEF5))
        else ->
            Triple(Icons.Default.Notifications, Color.Gray, Color.LightGray)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                if (notification.isRead)
                    MaterialTheme.colorScheme.surface
                else
                    MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(bg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = tint)
            }

            Column {
                Text(
                    notification.title,
                    fontWeight =
                        if (!notification.isRead) FontWeight.Bold
                        else FontWeight.Normal
                )
                Text(notification.message, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}




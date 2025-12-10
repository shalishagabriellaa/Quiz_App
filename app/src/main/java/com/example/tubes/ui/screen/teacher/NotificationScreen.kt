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

data class NotificationItem(
    val id: Int,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
    val title: String,
    val description: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    var selectedNotificationId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMarkReadDialog by remember { mutableStateOf(false) }

    val notifications = remember {
        mutableStateListOf(
            NotificationItem(
                id = 1,
                icon = Icons.Default.Add,
                iconTint = Color(0xFF5B7C99),
                iconBackground = Color(0xFFE8EEF5),
                title = "New quiz submitted",
                description = "You have a new quiz that has been submitted.",
                time = "2 hours ago"
            ),
            NotificationItem(
                id = 2,
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF5B7C99),
                iconBackground = Color(0xFFE8EEF5),
                title = "Score updated",
                description = "Your quiz score has been updated.",
                time = "1 hour ago"
            ),
            NotificationItem(
                id = 3,
                icon = Icons.Default.Notifications,
                iconTint = Color(0xFFE57373),
                iconBackground = Color(0xFFFFEBEE),
                title = "Reminder",
                description = "Don't forget to take your quiz tomorrow.",
                time = "4 hours ago"
            ),
            NotificationItem(
                id = 4,
                icon = Icons.Default.Check,
                iconTint = Color(0xFFE57373),
                iconBackground = Color(0xFFFFEBEE),
                title = "Performance analyzed",
                description = "Your quiz performance has been analyzed.",
                time = "3 hours ago"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notification bell */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            // Notifications header
            Text(
                text = "Notifications",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF2D3E50)
            )

            // Notifications list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        isSelected = selectedNotificationId == notification.id,
                        onClick = {
                            selectedNotificationId = notification.id
                            // Handle notification click - could navigate to detail screen
                        }
                    )
                }
            }

            // Bottom buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF5B6B7C)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete Notifications", fontSize = 15.sp)
                }

                Button(
                    onClick = { showMarkReadDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5B6B9E)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mark All as Read", fontSize = 15.sp)
                }
            }
        }

        // Delete Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFE57373)
                    )
                },
                title = { Text("Delete All Notifications") },
                text = { Text("Are you sure you want to delete all notifications? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            notifications.clear()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFE57373)
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Mark as Read Dialog
        if (showMarkReadDialog) {
            AlertDialog(
                onDismissRequest = { showMarkReadDialog = false },
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF5B7C99)
                    )
                },
                title = { Text("Mark All as Read") },
                text = { Text("All notifications will be marked as read.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Handle mark as read logic
                            showMarkReadDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showMarkReadDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
//                indication = rememberRipple(color = Purple40),
                onClick = onClick
            ),
        color = if (isSelected) Color(0xFFF5F5F5) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = notification.iconBackground,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = notification.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3E50)
                )
                Text(
                    text = notification.description,
                    fontSize = 13.sp,
                    color = Color(0xFF7A8A99),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Time
            Text(
                text = notification.time,
                fontSize = 12.sp,
                color = Color(0xFF9CA8B4)
            )
        }
    }
}

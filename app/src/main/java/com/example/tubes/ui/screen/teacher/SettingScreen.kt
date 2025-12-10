package com.example.tubes.ui.screen.teacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.LocalRippleTheme
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

data class SettingItem(
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {
    var showLogoutDialog by remember { mutableStateOf(false) }

    val accountSettings = listOf(
        SettingItem(
            icon = Icons.Default.Person,
            iconBackground = Color(0xFFE3F2FD),
            iconTint = Color(0xFF5B9BD5),
            title = "Profile Info",
            description = "View and edit your profile"
        ),
        SettingItem(
            icon = Icons.Default.Lock,
            iconBackground = Color(0xFFFFF3E0),
            iconTint = Color(0xFFFFB74D),
            title = "Password",
            description = "Change your password"
        ),
        SettingItem(
            icon = Icons.Default.Email,
            iconBackground = Color(0xFFE8EAF6),
            iconTint = Color(0xFF7986CB),
            title = "Linked Email",
            description = "Update your email address"
        )
    )

    val privacySettings = listOf(
        SettingItem(
            icon = Icons.Default.Lock,
            iconBackground = Color(0xFFFFF3E0),
            iconTint = Color(0xFFFFB74D),
            title = "Data Visibility",
            description = "Control who sees your data"
        ),
        SettingItem(
            icon = Icons.Default.Warning,
            iconBackground = Color(0xFFFFEBEE),
            iconTint = Color(0xFFE57373),
            title = "Access Permissions",
            description = "Manage app access"
        )
    )

    val appSettings = listOf(
        SettingItem(
            icon = Icons.Default.Settings,
            iconBackground = Color(0xFFFFF3E0),
            iconTint = Color(0xFFFFB74D),
            title = "Theme Mode",
            description = "Switch between light and dark mode"
        ),
        SettingItem(
            icon = Icons.Default.LocationOn,
            iconBackground = Color(0xFFE0F2F1),
            iconTint = Color(0xFF4DB6AC),
            title = "Language",
            description = "Select your preferred language"
        )
    )

    val notificationSettings = listOf(
        SettingItem(
            icon = Icons.Default.Notifications,
            iconBackground = Color(0xFFFFF9C4),
            iconTint = Color(0xFFFDD835),
            title = "Push Notifications",
            description = "Toggle for push alerts"
        ),
        SettingItem(
            icon = Icons.Default.Email,
            iconBackground = Color(0xFFE8EAF6),
            iconTint = Color(0xFF7986CB),
            title = "Email Alerts",
            description = "Toggle for email updates"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Account Settings
                item {
                    SectionHeader("Account Settings")
                }
                item {
                    accountSettings.forEach { setting ->
                        SettingItemRow(setting)
                    }
                }

                // Privacy Settings
                item {
                    SectionHeader("Privacy Settings")
                }
                item {
                    privacySettings.forEach { setting ->
                        SettingItemRow(setting)
                    }
                }

                // App Settings
                item {
                    SectionHeader("App Settings")
                }
                item {
                    appSettings.forEach { setting ->
                        SettingItemRow(setting)
                    }
                }

                // Notification Preferences
                item {
                    SectionHeader("Notification Preferences")
                }
                item {
                    notificationSettings.forEach { setting ->
                        SettingItemRow(setting)
                    }
                }
            }

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D3E50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Logout", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Logout Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            // Handle logout
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF2D3E50),
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp, end = 16.dp)
    )
}

@Composable
fun SettingItemRow(setting: SettingItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ },
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = setting.iconBackground,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = setting.icon,
                    contentDescription = null,
                    tint = setting.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = setting.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3E50)
                )
                Text(
                    text = setting.description,
                    fontSize = 13.sp,
                    color = Color(0xFF7A8A99),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFFB0BEC5),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

package com.example.tubes.ui.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBack: () -> Unit = {},
    onPersonalInfo: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onHelpCenter: () -> Unit = {},
    onAboutQuorri: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlue
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingMenuItem(
                    icon = Icons.Filled.Person,
                    title = "Personal Info",
                    backgroundColor = Color(0xFFFBDCA0),
                    iconTint = Color(0xFFFF9800),
                    onClick = onPersonalInfo
                )
            }

            item {
                SettingMenuItem(
                    icon = Icons.Filled.Lock,
                    title = "Change Password",
                    backgroundColor = Color(0xFFCFF6D5),
                    iconTint = Color(0xFF4CAF50),
                    onClick = onChangePassword
                )
            }

            item {
                SettingMenuItem(
                    icon = Icons.Filled.Help,
                    title = "Help Center",
                    backgroundColor = Color(0xFFD1E7FF),
                    iconTint = Color(0xFF2196F3),
                    onClick = onHelpCenter
                )
            }

            item {
                SettingMenuItem(
                    icon = Icons.Filled.Info,
                    title = "About Quorri",
                    backgroundColor = Color(0xFFF5F2A7),
                    iconTint = Color(0xFFFBC02D),
                    onClick = onAboutQuorri
                )
            }

            item {
                SettingMenuItem(
                    icon = Icons.Filled.Logout,
                    title = "Logout",
                    backgroundColor = Color(0xFFF8B5B5),
                    iconTint = Color(0xFFEF5350),
                    textColor = Color(0xFFEF5350),
                    onClick = onLogout
                )
            }
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

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}

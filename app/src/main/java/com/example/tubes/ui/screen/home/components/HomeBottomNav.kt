package com.example.tubes.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeBottomNav(
    modifier: Modifier = Modifier,
    onHome: () -> Unit = {},
    onQuizzes: () -> Unit = {},
    onQR: () -> Unit = {},
    onLeaderboard: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
    ) {
        // Bar container dengan rounded top
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF162471))
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Item("Home", Icons.Filled.Home, onHome)
                Item("Quizzes", null, onQuizzes)
                Spacer(Modifier.width(56.dp)) // ruang untuk QR di tengah
                Item("Leaderboard", Icons.Filled.Leaderboard, onLeaderboard)
                Item("Profile", Icons.Filled.Person, onProfile)
            }
        }

        // QR floating di tengah
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .shadow(8.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onQR() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.QrCode, contentDescription = "QR", tint = Color(0xFF162471))
        }
    }
}

@Composable
private fun Item(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (icon != null) Icon(icon, contentDescription = text, tint = Color.White)
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

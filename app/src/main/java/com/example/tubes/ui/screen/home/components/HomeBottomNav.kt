package com.example.tubes.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(Color(0xFF162471))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Item("Home", Icons.Filled.Home)
        Item("Quizzes", null) { onQuizzes() }
        CenterQR { onQR() }
        Item("Leaderboard", Icons.Filled.Leaderboard)
        Item("Profile", Icons.Filled.Person)
    }
}

@Composable private fun Item(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector?, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (icon != null) Icon(icon, contentDescription = text, tint = Color.White)
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

@Composable private fun CenterQR(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.QrCode, contentDescription = "QR", tint = Color(0xFF162471))
    }
}

package com.example.tubes.ui.teacher.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class TeacherBottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : TeacherBottomNavItem("dashboard", "Dashboard", Icons.Default.Home)
    object Quizzes : TeacherBottomNavItem("quizzes", "Quizzes", Icons.Default.List)
    object Bank : TeacherBottomNavItem("bank", "Bank", Icons.Default.AccountBalance)
    object Monitoring : TeacherBottomNavItem("monitoring", "Monitoring", Icons.Default.Analytics)
    object Profile : TeacherBottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun TeacherBottomNavigation(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        TeacherBottomNavItem.Dashboard,
        TeacherBottomNavItem.Quizzes,
        TeacherBottomNavItem.Bank,
        TeacherBottomNavItem.Monitoring,
        TeacherBottomNavItem.Profile
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color(0xFF2E3856),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                TeacherBottomNavItemView(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
fun TeacherBottomNavItemView(
    item: TeacherBottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) Color(0xFFFFC107) else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (isSelected) Color(0xFF2E3856) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

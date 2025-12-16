package com.example.tubes.ui.teacher.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

private val BarPurple = Color(0xFF4C4FA4)
private val DeepBlue = Color(0xFF162471)
private val GoldActive = Color(0xFFF4D488)

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BarPurple)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                clip = false
            )
            .background(
                color = BarPurple,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .height(86.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
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

        // Bottom indicator bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-8).dp)
                .size(width = 120.dp, height = 6.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.95f))
        )
    }
}

@Composable
fun TeacherBottomNavItemView(
    item: TeacherBottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val lift by animateDpAsState(
        targetValue = if (isSelected) 35.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "lift"
    )

    val circleSize by animateDpAsState(
        targetValue = if (isSelected) 64.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "circleSize"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "borderWidth"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f,
        label = "iconAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 60.dp)
            .offset(y = -lift)
            .clickable { onClick() }
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(GoldActive)
                    .border(borderWidth, DeepBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentColor provides DeepBlue) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        } else {
            CompositionLocalProvider(LocalContentColor provides Color.White.copy(alpha = iconAlpha)) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

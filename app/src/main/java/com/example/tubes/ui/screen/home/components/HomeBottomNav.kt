package com.example.tubes.ui.screen.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
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

/* =========================
   PUBLIC API
   ========================= */

enum class BottomTab(val label: String) {
    Home("Home"), Quizzes("Quizzes"), Leaderboard("Leaderboard"), Profile("Profile")
}

@Composable
fun QuizBottomBar(
    selected: BottomTab,
    onSelected: (BottomTab) -> Unit,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        // Bar container (rounded top)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(86.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(BarPurple)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BarItem(
                    text = BottomTab.Home.label,
                    icon = Icons.Filled.Home,
                    selected = selected == BottomTab.Home
                ) { onSelected(BottomTab.Home) }

                BarItem(
                    text = BottomTab.Quizzes.label,
                    icon = Icons.Outlined.GridView,
                    selected = selected == BottomTab.Quizzes
                ) { onSelected(BottomTab.Quizzes) }

                Spacer(Modifier.width(64.dp)) // ruang tombol QR di tengah

                BarItem(
                    text = BottomTab.Leaderboard.label,
                    icon = Icons.Filled.Leaderboard,
                    selected = selected == BottomTab.Leaderboard
                ) { onSelected(BottomTab.Leaderboard) }

                BarItem(
                    text = BottomTab.Profile.label,
                    icon = Icons.Filled.Person,
                    selected = selected == BottomTab.Profile
                ) { onSelected(BottomTab.Profile) }
            }

            // "home indicator" putih (garis kecil) di bawah
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-8).dp)
                    .size(width = 120.dp, height = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.95f))
            )
        }

        // Tombol QR mengambang di tengah (dengan border & lift saat ditekan)
        QrCenterButton(
            modifier = Modifier.align(Alignment.TopCenter),
            onClick = onQrClick
        )
    }
}

/* =========================
   PRIVATE: ITEM & QR BUTTON
   ========================= */

@Composable
private fun BarItem(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Animasi “naik” & ukuran lingkaran aktif
    val lift by animateDpAsState(
        targetValue = if (selected) 35.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val circleSize by animateDpAsState(
        targetValue = if (selected) 64.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    val borderWidth by animateDpAsState(
        targetValue = if (selected) 5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.95f
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 70.dp)
            .offset(y = -lift) // ← efek “ikon naik”
            .clickable { onClick() }
    ) {
        if (selected) {
            // Lingkaran aktif lebih besar + ring biru + shadow
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
                    Icon(icon, contentDescription = text, modifier = Modifier.size(22.dp))
                }
            }
        } else {
            CompositionLocalProvider(LocalContentColor provides Color.White.copy(alpha = iconAlpha)) {
                Icon(icon, contentDescription = text)
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun QrCenterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animasi “naik” saat ditekan (press state)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val lift by animateDpAsState(
        targetValue = if (pressed) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    Box(
        modifier = modifier
            .offset(y = -lift) // ← naik saat ditekan
            .size(68.dp)       // sedikit lebih besar
            .shadow(14.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(Color.White)
            .border(width = 4.dp, color = DeepBlue, shape = CircleShape) // ← BORDER QR
            .clickable(
                interactionSource = interaction,
                indication = null // biar clean, tanpa ripple (opsional)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.QrCode,
            contentDescription = "QR",
            tint = DeepBlue,
            modifier = Modifier.size(30.dp)
        )
    }
}

/* =========================
   COLORS
   ========================= */
private val BarPurple  = Color(0xFF4C4FA4)
private val DeepBlue   = Color(0xFF162471)
private val GoldActive = Color(0xFFF4D488)

package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.R // Pastikan R diimpor, digunakan untuk Placeholder
import com.example.tubes.data.model.TeacherQuizUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Fungsi yang Anda sediakan, diubah untuk menggunakan warna RGB dari screenshot
private fun statusColor(status: String): Color {
    return when (status.lowercase()) {
        "active" -> Color(0xFF70CF7E)    // Hijau (digunakan sebagai "Active" di kartu)
        "draft" -> Color(0xFFEE8A4E)
        "completed" -> Color(0xFF70CF7E)
        else -> Color.Gray
    }
}

@Composable
fun QuizCardItem(
    quiz: TeacherQuizUi,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // --- 1. Banner dan Qs Tag ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                if (quiz.bannerUrl != null) {
                    AsyncImage(
                        model = quiz.bannerUrl,
                        contentDescription = "Quiz Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder saat bannerUrl null
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Banner", color = Color.Gray)
                    }
                }

                // Qs Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(0xCC000000), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${quiz.totalQuestions} Qs",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- 2. Detail Konten ---
            Column(modifier = Modifier.padding(16.dp)) {
                // Actions Row dan Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(label = "Detail", icon = Icons.Default.Info, color = Color(0xFF4C75FF), onClick = onDetailClick)
                        ActionButton(label = "Edit", icon = Icons.Default.Edit, color = Color(0xFFEE8A4E), onClick = onEditClick)
                        ActionButton(label = "Delete", icon = Icons.Default.Delete, color = Color.Red, onClick = onDeleteClick)
                    }
                    Text(
                        // Menggunakan warna hijau terang dari status completed/active pada screenshot
                        text = quiz.status.replaceFirstChar { it.titlecase(Locale.ROOT) },
                        color = Color(0xFF70CF7E),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = quiz.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF282C4C)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Created Date
                Text(
                    text = "Created: ${formatDate(quiz.createdAtMillis)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                // Author (Denormalized)
                Text(
                    text = quiz.authorName,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatsPill(label = "${quiz.totalParticipants} Participants", icon = Icons.Default.People, color = Color(0xFF4C75FF))
                    StatsPill(label = "Avg Score ${"%.0f".format(quiz.averageScore)}", icon = Icons.Default.Star, color = Color(0xFF6B58E9))
                }
            }
        }
    }
}


@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatsPill(label: String, icon: ImageVector, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
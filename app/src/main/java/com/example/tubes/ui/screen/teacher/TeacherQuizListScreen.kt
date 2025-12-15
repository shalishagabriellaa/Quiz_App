package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tubes.data.model.TeacherQuizUi
import com.example.tubes.viewmodel.TeacherQuizListViewModel // Asumsi ViewModel Anda
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- FUNGSI UTAMA SCREEN ---

@Composable
fun TeacherQuizListScreen(
    authorId: String,
    viewModel: TeacherQuizListViewModel = viewModel(),
    onAddQuizClick: () -> Unit,
    onEditQuizClick: (String) -> Unit   // ⬅️ INI KUNCI

) {
    val quizzes by viewModel.quizzes.collectAsState()

    // Data status statis atau dari ViewModel jika ada
    val statusCounts by viewModel.statusCounts.collectAsState()

    LaunchedEffect(authorId) {
        viewModel.load(authorId)
    }

    Scaffold(
        containerColor = Color(0xFF282C4C) // Warna gelap dari screenshot
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF282C4C))
        ) {
            item {
                // Header Title
                Text(
                    text = "My Quizzes",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)
                )

                // Search Bar
                QuizSearchBar(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
            }

            item {

                AddQuizButton(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    onClick = onAddQuizClick
                )
            }

            item {
                // Quiz Status Row (Menggunakan data statis/hipotesis statusCounts)
                QuizStatusRow(
                    statusCounts = statusCounts,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            items(quizzes) { quiz ->
                QuizCardItem(
                    quiz = quiz,
                    onDetailClick = { },
                    onEditClick = {
                        onEditQuizClick(quiz.id)
                    },
                    onDeleteClick = { viewModel.delete(quiz.id, authorId) }
                )
            }
        }
    }
}


@Composable
fun QuizSearchBar(modifier: Modifier = Modifier) {
    // Menggunakan implementasi Material 3 yang disesuaikan
    OutlinedTextField(
        value = "Search your quiz...",
        onValueChange = { /* Implementasi pencarian */ },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.Gray)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            cursorColor = Color(0xFF4C75FF)
        ),
        textStyle = LocalTextStyle.current.copy(color = Color.Gray)
    )
}
@Composable
fun AddQuizButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4C75FF)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add new quiz",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Add new quiz",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
fun QuizStatusRow(statusCounts: Map<String, Int>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Active
        StatusItem(
            color = Color(0xFF6B58E9), // Active Blue
            count = statusCounts["active"] ?: 0,
            label = "Quiz Active"
        )
        // Draft
        StatusItem(
            color = Color(0xFFEE8A4E), // Draft Orange
            count = statusCounts["draft"] ?: 0,
            label = "Draft Quiz"
        )
        // Completed
        StatusItem(
            color = Color(0xFF70CF7E), // Completed Green
            count = statusCounts["completed"] ?: 0,
            label = "Completed"
        )
    }
}

@Composable
fun StatusItem(color: Color, count: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$count", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
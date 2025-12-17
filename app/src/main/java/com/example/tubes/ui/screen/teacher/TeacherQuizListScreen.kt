package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tubes.data.model.TeacherQuizUi
import com.example.tubes.viewmodel.TeacherQuizListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ===== Color palette (mirip screenshot) =====
private val PageBg = Color(0xFF282C4C)
private val HeaderBg = Color(0xFF1F2343)
private val SearchBg = Color(0xFFD2D5EA)
private val PrimaryBtn = Color(0xFF4C54B5)

@Composable
fun TeacherQuizListScreen(
    authorId: String,
    viewModel: TeacherQuizListViewModel = viewModel(),
    onAddQuizClick: () -> Unit,
    onEditQuizClick: (String) -> Unit,
    onGenerateQrClick: (String) -> Unit
) {
    val quizzes by viewModel.quizzes.collectAsState()
    val statusCounts by viewModel.statusCounts.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    // UI-only query (tampilan/search doang)
    var query by remember { mutableStateOf("") }

    LaunchedEffect(authorId) {
        viewModel.load(authorId)
    }

    val filteredQuizzes = remember(quizzes, query) {
        if (query.isBlank()) quizzes
        else quizzes.filter { it.title.contains(query, ignoreCase = true) }
    }

    Scaffold(containerColor = PageBg) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PageBg)
        ) {
            item {
                HeaderSection(
                    query = query,
                    onQueryChange = { query = it }
                )
            }

            item {
                AddQuizButtonModern(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    onClick = onAddQuizClick
                )
            }

            item {
                QuizStatusCard(
                    statusCounts = statusCounts,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(Modifier.height(8.dp)) }

            items(filteredQuizzes, key = { it.id }) { quiz ->
                QuizCardModern(
                    quiz = quiz,
                    onDetailClick = { onGenerateQrClick(quiz.id) }, // fungsi tetap sama
                    onEditClick = { onEditQuizClick(quiz.id) },      // fungsi tetap sama
                    onDeleteClick = { viewModel.onDeleteClick(quiz) },
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            item { Spacer(Modifier.height(110.dp)) }
        }
    }

    // ===== Delete confirm dialog (tetap pakai yang kamu punya) =====
    if (deleteState.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Hapus Quiz") },
            text = { Text("Yakin ingin menghapus quiz \"${deleteState.quizTitle}\"?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmDelete(authorId) },
                    enabled = !deleteState.isDeleting
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDelete() }) { Text("Batal") }
            }
        )
    }
}

/* =========================
   HEADER (rounded bottom)
   ========================= */

@Composable
private fun HeaderSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(HeaderBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            CircleOutlineIcon(Icons.Default.Settings)
//
//            Spacer(Modifier.width(12.dp))

            Text(
                text = "My Quizzes",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

//            CircleOutlineIcon(Icons.Default.Notifications)
//
//            Spacer(Modifier.width(10.dp))

//            // Avatar dekor UI (bukan fitur baru)
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .background(
//                        Brush.linearGradient(
//                            listOf(Color(0xFFFFC1D6), Color(0xFFFFE5B4))
//                        )
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("A", color = Color(0xFF2B2F55), fontWeight = FontWeight.Bold)
//            }
    }

        Spacer(Modifier.height(14.dp))

        SearchPill(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun CircleOutlineIcon(icon: ImageVector) {
    IconButton(
        onClick = { /* UI only */ },
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SearchPill(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = Color(0xFF545A86)
            )
        },
        placeholder = {
            Text(
                "Search your quiz...",
                color = Color(0xFF545A86),
                fontSize = 13.sp
            )
        },
        shape = RoundedCornerShape(999.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SearchBg,
            focusedContainerColor = SearchBg,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            cursorColor = PrimaryBtn,
            focusedTextColor = Color(0xFF1D2140),
            unfocusedTextColor = Color(0xFF1D2140)
        ),

    )
}

/* =========================
   ADD BUTTON
   ========================= */

@Composable
private fun AddQuizButtonModern(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBtn),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = "Add new quiz",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

/* =========================
   STATUS CARD
   ========================= */

@Composable
private fun QuizStatusCard(
    statusCounts: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            StatusBlock(
                dotColor = Color(0xFF5A55D6),
                label = "Quiz Active",
                count = statusCounts["active"] ?: 0
            )

            Divider(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp),
                color = Color.Black.copy(alpha = 0.08f)
            )

            StatusBlock(
                dotColor = Color(0xFFEE8A4E),
                label = "Draft Quiz",
                count = statusCounts["draft"] ?: 0
            )

            Divider(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp),
                color = Color.Black.copy(alpha = 0.08f)
            )

            StatusBlock(
                dotColor = Color(0xFF2ECC71),
                label = "Completed",
                count = statusCounts["completed"] ?: 0
            )
        }
    }
}

@Composable
private fun StatusBlock(
    dotColor: Color,
    label: String,
    count: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color(0xFF3A3D5C))
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = dotColor
            )
        }
    }
}

/* =========================
   QUIZ CARD (rapi seperti mockup)
   ========================= */

@Composable
private fun QuizCardModern(
    quiz: TeacherQuizUi,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // ===== Banner kiri: pakai bannerUrl kalau ada, kalau tidak placeholder =====
                Box(
                    modifier = Modifier
                        .width(118.dp)
                        .height(118.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFE6EA), Color(0xFFE6F0FF))
                            )
                        )
                ) {
                    if (!quiz.bannerUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = quiz.bannerUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // placeholder cantik (UI only)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color(0xFF4B4F7A),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color(0xFF2B2F55), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${quiz.totalQuestions} Qs",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Created: ${formatCreatedDate(quiz.createdAtMillis)}",
                            fontSize = 11.sp,
                            color = Color(0xFF7B7E99),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        StatusPill(quiz.status)
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = quiz.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14162B),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = quiz.authorName,
                        fontSize = 12.sp,
                        color = Color(0xFF7B7E99),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip(
                            text = "${quiz.totalParticipants} Participants",
                            bg = Color(0xFFEDE7FF),
                            fg = Color(0xFF6B58E9)
                        )
                        Chip(
                            text = "Avg score ${quiz.averageScore.toInt()}",
                            bg = Color(0xFFF0E9FF),
                            fg = Color(0xFF7E57C2)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionOutlined(
                    text = "QR",
                    icon = Icons.Default.Visibility,
                    color = Color(0xFF2B2F55),
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f)
                )

                // ===== INI BAGIAN YANG DIUBAH =====
                // Cek apakah status kuis adalah "draft" (tidak case-sensitive)
                val isEditable = quiz.status.equals("draft", ignoreCase = true)

                ActionOutlined(
                    text = "Edit",
                    icon = Icons.Default.Edit,
                    color = Color(0xFFEE8A4E),
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    enabled = isEditable // <-- TERAPKAN logikanya di sini
                )
                // ====================================

                ActionOutlined(
                    text = "Delete",
                    icon = Icons.Default.Delete,
                    color = Color(0xFFE53935),
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val s = status.lowercase()
    val (bg, fg) = when (s) {
        "active" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "draft" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "completed" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        else -> Color(0xFFF1F1F6) to Color(0xFF5A5D77)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Chip(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = fg,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActionOutlined(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // <-- 1. TAMBAHKAN parameter ini
) {
    // 2. Tentukan warna berdasarkan status enabled
    val finalColor = if (enabled) color else Color.Gray.copy(alpha = 0.5f)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(10.dp),
        // 3. Gunakan warna yang sudah ditentukan untuk border dan konten
        border = BorderStroke(1.dp, finalColor),
        contentPadding = PaddingValues(horizontal = 10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = finalColor
        ),
        enabled = enabled // <-- 4. TERAPKAN status enabled pada tombol
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}


/* =========================
   DATE FORMAT (model pakai createdAtMillis)
   ========================= */

private fun formatCreatedDate(millis: Long?): String {
    if (millis == null) return "-"
    val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return df.format(Date(millis))
}

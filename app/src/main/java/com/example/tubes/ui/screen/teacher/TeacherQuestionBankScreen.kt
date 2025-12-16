package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.data.model.Difficulty
import com.example.tubes.data.model.TeacherQuestionBank
import com.example.tubes.viewmodel.TeacherQuestionBankViewModel

// ===== Palette (nyambung sama screen lain kamu) =====
private val TopBarColor = Color(0xFF252A57)
private val PageBgTop = Color(0xFFF6F2FF)
private val PageBgBottom = Color(0xFFF2F4FF)
private val FieldBg = Color(0xFFE6E8F6)
private val FieldStroke = Color(0xFFE8E8F3)
private val MutedText = Color(0xFF7A7F9A)
private val CardStroke = Color(0xFFE9EAF5)
private val Danger = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherQuestionBankScreen(
    viewModel: TeacherQuestionBankViewModel
) {
    val allQuestions by viewModel.questions.collectAsState()

    // UI state
    var query by remember { mutableStateOf("") }
    var difficultyExpanded by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }

    // delete modal state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<TeacherQuestionBank?>(null) }

    // apply filters
    val filtered = remember(allQuestions, query, selectedDifficulty) {
        allQuestions
            .asSequence()
            .filter { item ->
                selectedDifficulty == null || item.difficulty == selectedDifficulty
            }
            .filter { item ->
                query.isBlank() || item.questionText.contains(query, ignoreCase = true)
            }
            .toList()
    }

    // stats
    val averageDifficultyLabel = remember(filtered) {
        if (filtered.isEmpty()) "-" else {
            fun scoreOf(d: Difficulty): Int = when (d) {
                Difficulty.EASY -> 1
                Difficulty.MEDIUM -> 2
                Difficulty.HARD -> 3
                Difficulty.EXTREME -> 4
            }
            val avg = filtered.map { scoreOf(it.difficulty).toDouble() }.average()
            when {
                avg < 1.5 -> "Easy"
                avg < 2.5 -> "Medium"
                avg < 3.5 -> "Hard"
                else -> "Extreme"
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Questions Bank",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PageBgTop, PageBgBottom)))
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

//            // Search (biar ga kepotong: height cukup + singleLine + padding aman)
//            OutlinedTextField(
//                value = query,
//                onValueChange = { query = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp),
//                singleLine = true,
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search",
//                        tint = Color(0xFF545A86)
//                    )
//                },
//                placeholder = {
////                    Text(
////                        "Search your questions",
////                        color = Color(0xFF545A86),
////                        fontSize = 13.sp,
////                        maxLines = 1,
////                        overflow = TextOverflow.Ellipsis
////                    )
//                },
//                shape = RoundedCornerShape(999.dp),
//                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
//                    focusedContainerColor = FieldBg,
//                    unfocusedContainerColor = FieldBg,
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent,
//                    cursorColor = TopBarColor,
//                    focusedTextColor = Color(0xFF1D2140),
//                    unfocusedTextColor = Color(0xFF1D2140)
//                )
//            )

            Spacer(Modifier.height(12.dp))

            // Stats card (simple & rapi)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBlock(label = "Total Questions", value = filtered.size.toString())
                    Divider(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp),
                        color = Color.Black.copy(alpha = 0.07f)
                    )
                    StatBlock(label = "Average Difficulty", value = averageDifficultyLabel)
                    Divider(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp),
                        color = Color.Black.copy(alpha = 0.07f)
                    )
                    // kalau kamu punya "last updated" dari backend nanti tinggal ganti value ini
                    StatBlock(label = "Last Updated", value = "-")
                }
            }

            Spacer(Modifier.height(12.dp))

            // "Category" = Difficulty dropdown
            Text(
                text = "Category Quiz",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF14162B)
            )
            Spacer(Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = difficultyExpanded,
                onExpandedChange = { difficultyExpanded = !difficultyExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedDifficulty?.name
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = {
                        Text(
                            "Select difficulty",
                            color = MutedText,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = FieldStroke,
                        unfocusedBorderColor = FieldStroke
                    )
                )

                ExposedDropdownMenu(
                    expanded = difficultyExpanded,
                    onDismissRequest = { difficultyExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            selectedDifficulty = null
                            difficultyExpanded = false
                            viewModel.onDifficultySelected(null) // tetap sinkron ke VM
                        }
                    )
                    Difficulty.values().forEach { d ->
                        DropdownMenuItem(
                            text = {
                                Text(d.name.lowercase().replaceFirstChar { it.uppercase() })
                            },
                            onClick = {
                                selectedDifficulty = d
                                difficultyExpanded = false
                                viewModel.onDifficultySelected(d) // panggil VM kamu
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyBankState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filtered,
                        key = { "${it.quizId}_${it.id}" }
                    ) { item ->
                        TeacherQuestionBankCard(
                            item = item,
                            onDeleteClick = {
                                pendingDelete = item
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // ===== DELETE CONFIRM MODAL =====
    if (showDeleteDialog) {
        val item = pendingDelete
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                pendingDelete = null
            },
            title = { Text("Delete Question") },
            text = {
                Text(
                    text = if (item != null)
                        "Are you sure you want to delete this question?\n\n\"${item.questionText}\""
                    else
                        "Are you sure?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        item?.let { viewModel.delete(it) }
                        showDeleteDialog = false
                        pendingDelete = null
                    }
                ) {
                    Text("Delete", color = Danger, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        pendingDelete = null
                    }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = MutedText)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF252A57))
    }
}

@Composable
private fun TeacherQuestionBankCard(
    item: TeacherQuestionBank,
    onDeleteClick: () -> Unit
) {
    val correctAnswerText = remember(item) {
        if (item.correctAnswerIndex in item.options.indices) {
            item.options[item.correctAnswerIndex]
        } else "Invalid answer"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Text(
                text = item.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF14162B),
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Category: ${item.quizTitle}",
                    fontSize = 12.sp,
                    color = MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Difficulty: ${item.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = Color(0xFF3E4396),
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Correct Answer: $correctAnswerText",
                fontSize = 12.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Danger
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Danger),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun EmptyBankState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "No questions found",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

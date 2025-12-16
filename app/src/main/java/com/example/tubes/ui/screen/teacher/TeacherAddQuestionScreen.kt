@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tubes.ui.screen.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.viewmodel.TeacherAddQuestionViewModel

/* =========================
   COLOR PALETTE (konsisten)
   ========================= */
private val TopBarColor = Color(0xFF252A57)
private val PageBg = Color(0xFFF2F4FF)
private val PrimaryBtn = Color(0xFF3E4396)
private val Border = Color(0xFFE6E8F2)
private val MutedText = Color(0xFF7A7F9A)

@Composable
fun TeacherAddQuestionScreen(
    viewModel: TeacherAddQuestionViewModel,
    onFinished: () -> Unit,
    onPreview: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.finished) {
        if (viewModel.finished) {
            Toast.makeText(context, "Quiz berhasil dibuat", Toast.LENGTH_SHORT).show()
            onFinished()
        }
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Add Questions",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Build your quiz questions and define correct answers",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(999.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(Color.White.copy(alpha = 0.35f))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.12f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "See Preview",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            /* ================= STEPPER ================= */
            QuestionStepper(
                current = viewModel.currentIndex,
                total = viewModel.totalQuestions,
                onSelect = viewModel::goTo
            )

            Spacer(Modifier.height(16.dp))

            /* ================= QUESTION ================= */
            Text(
                text = "Question ${viewModel.currentIndex}",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = viewModel.form.questionText,
                onValueChange = {
                    viewModel.updateForm(viewModel.form.copy(questionText = it))
                },
                placeholder = { Text("Enter question text") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true, // ✅ INI PENTING
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryBtn
                )
            )

            Spacer(Modifier.height(16.dp))

            /* ================= OPTIONS ================= */
            Text(
                text = "Answer Options",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            viewModel.form.options.forEachIndexed { index, option ->
                TextField(
                    value = option,
                    onValueChange = {
                        val newOptions = viewModel.form.options.toMutableList()
                        newOptions[index] = it
                        viewModel.updateForm(
                            viewModel.form.copy(options = newOptions)
                        )
                    },
                    placeholder = { Text("Option ${'A' + index}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true, // ✅ INI PENTING
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryBtn
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            /* ================= CORRECT ANSWER ================= */
            Text(
                text = "Correct Answer",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (0..3).forEach { index ->
                    val selected = viewModel.form.correctAnswerIndex == index

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (selected) PrimaryBtn else Color.White,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                viewModel.updateForm(
                                    viewModel.form.copy(correctAnswerIndex = index)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ('A' + index).toString(),
                            color = if (selected) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ================= EXPLANATION ================= */
            TextField(
                value = viewModel.form.explanation,
                onValueChange = {
                    viewModel.updateForm(
                        viewModel.form.copy(explanation = it)
                    )
                },
                placeholder = { Text("Explanation") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true, // ✅ INI PENTING
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryBtn
                )
            )

            Spacer(Modifier.height(24.dp))

            /* ================= ACTION BUTTON ================= */
            Button(
                onClick = {
                    viewModel.nextOrFinish { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBtn
                )
            ) {
                Text(
                    text =
                        if (viewModel.currentIndex == viewModel.totalQuestions)
                            "Finish"
                        else
                            "Next",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/* =========================
   STEPPER
   ========================= */

@Composable
private fun QuestionStepper(
    current: Int,
    total: Int,
    onSelect: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            val index = i + 1
            val active = index == current

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (active) PrimaryBtn else Color.White,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    color = if (active) Color.White else Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

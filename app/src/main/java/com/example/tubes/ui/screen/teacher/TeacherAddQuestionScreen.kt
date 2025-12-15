package com.example.tubes.ui.screen.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tubes.viewmodel.TeacherAddQuestionViewModel

@Composable

fun TeacherAddQuestionScreen(
    viewModel: TeacherAddQuestionViewModel,
    onFinished: () -> Unit,
    onPreview: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.finished) {
        if (viewModel.finished) {
            Toast.makeText(context, "Quiz berhasil dibuat", Toast.LENGTH_SHORT).show()
            onFinished()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        // STEPPER
        QuestionStepper(
            current = viewModel.currentIndex,
            total = viewModel.totalQuestions,
            onSelect = viewModel::goTo
        )

        Spacer(Modifier.height(16.dp))

        // QUESTION
        Text("Question ${viewModel.currentIndex}")

        TextField(
            value = viewModel.form.questionText,
            onValueChange = {
                viewModel.updateForm(viewModel.form.copy(questionText = it))
            },
            label = { Text("Question Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // OPTIONS A–D
        Text("Answer Options")

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
                label = { Text("Option ${'A' + index}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // CORRECT ANSWER (DI BAWAH OPTIONS)
        Text("Correct Answer")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            (0..3).forEach { index ->
                val selected = viewModel.form.correctAnswerIndex == index

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (selected) Color(0xFF4C75FF) else Color.LightGray,
                            RoundedCornerShape(8.dp)
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
                        color = if (selected) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // EXPLANATION
        TextField(
            value = viewModel.form.explanation,
            onValueChange = {
                viewModel.updateForm(
                    viewModel.form.copy(explanation = it)
                )
            },
            label = { Text("Explanation") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(24.dp))

        // BUTTONS
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            Button(onClick = onPreview) {
                Text("Preview")
            }

            Button(onClick = {
                viewModel.nextOrFinish { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(
                    if (viewModel.currentIndex == viewModel.totalQuestions)
                        "Finish"
                    else
                        "Next"
                )
            }
        }
    }
}


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
                        if (active) Color(0xFF4C75FF) else Color.LightGray,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    color = if (active) Color.White else Color.Black
                )
            }
        }
    }
}
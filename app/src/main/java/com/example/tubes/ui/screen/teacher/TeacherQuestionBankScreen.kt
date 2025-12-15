package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tubes.data.model.Difficulty
import com.example.tubes.data.model.TeacherQuestionBank
import com.example.tubes.viewmodel.TeacherQuestionBankViewModel

@Composable
fun TeacherQuestionBankScreen(
    viewModel: TeacherQuestionBankViewModel
) {
    val questions by viewModel.questions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Question Bank",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Total Questions: ${questions.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        DifficultyFilterRow(
            onSelected = viewModel::onDifficultySelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (questions.isEmpty()) {
            EmptyBankState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = questions,
                    key = { "${it.quizId}_${it.id}" }
                )
                { item ->
                    TeacherQuestionBankCard(
                        item = item,
                        onDelete = {
                            viewModel.delete(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyFilterRow(
    onSelected: (Difficulty?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        FilterChip(
            selected = false,
            onClick = { onSelected(null) },
            label = { Text("All") }
        )

        Difficulty.values().forEach { difficulty ->
            FilterChip(
                selected = false,
                onClick = { onSelected(difficulty) },
                label = { Text(difficulty.name) }
            )
        }
    }
}

@Composable
fun TeacherQuestionBankCard(
    item: TeacherQuestionBank,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            // Question text
            Text(
                text = item.questionText,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Metadata
            Text(
                text = "Quiz: ${item.quizTitle}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Difficulty: ${item.difficulty}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            val correctAnswerText = remember(item) {
                if (
                    item.correctAnswerIndex in item.options.indices
                ) {
                    item.options[item.correctAnswerIndex]
                } else {
                    "Invalid answer"
                }
            }

            Text(
                text = "Correct Answer: $correctAnswerText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(10.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun EmptyBankState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No questions found",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



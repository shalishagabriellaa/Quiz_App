package com.example.tubes.ui.screen.teacher

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tubes.viewmodel.TeacherCreateQuizViewModel
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long?): String {
    if (millis == null) return ""
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCreateQuizScreen(
    authorId: String,
    viewModel: TeacherCreateQuizViewModel,
    onNavigateToAddQuestions: (String, Int) -> Unit
) {
    val form by viewModel.form.collectAsState()
    Log.d("EDIT_DEBUG", "SCREEN COMPOSED")
    // ---------- Dummy Data ----------
    val categories = listOf(
        "Math",
        "Science",
        "History",
        "Programming"
    )

    val difficulties = listOf(
        "easy",
        "medium",
        "hard",
        "extreme"
    )

    // ---------- Image Picker ----------
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateForm(form.copy(bannerUri = it))
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {

        /* ================= TITLE ================= */
        TextField(
            value = form.title,
            onValueChange = {
                viewModel.updateForm(form.copy(title = it))
            },
            label = { Text("Quiz Title") },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        /* ================= DESCRIPTION ================= */
        TextField(
            value = form.description,
            onValueChange = {
                viewModel.updateForm(form.copy(description = it))
            },
            label = { Text("Description") },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        /* ================= CATEGORY DROPDOWN ================= */
        var categoryExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            TextField(
                value = form.categoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .padding(bottom = 12.dp)
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            viewModel.updateForm(
                                form.copy(
                                    categoryName = category,
                                    categoryId = category.lowercase()
                                )
                            )
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        /* ================= DURATION ================= */
        TextField(
            value = form.durationMinutes,
            onValueChange = {
                viewModel.updateForm(form.copy(durationMinutes = it))
            },
            label = { Text("Duration (minutes)") },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        TextField(
            value = form.totalQuestions,
            onValueChange = {
                if (it.all { ch -> ch.isDigit() }) {
                    viewModel.updateForm(form.copy(totalQuestions = it))
                }
            },
            label = { Text("Total Questions") },
            modifier = Modifier.padding(bottom = 12.dp)
        )


        /* ================= DIFFICULTY DROPDOWN ================= */
        var difficultyExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = difficultyExpanded,
            onExpandedChange = { difficultyExpanded = !difficultyExpanded }
        ) {
            TextField(
                value = form.difficulty,
                onValueChange = {},
                readOnly = true,
                label = { Text("Difficulty Level") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .padding(bottom = 12.dp)
            )

            ExposedDropdownMenu(
                expanded = difficultyExpanded,
                onDismissRequest = { difficultyExpanded = false }
            ) {
                difficulties.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level.capitalize()) },
                        onClick = {
                            viewModel.updateForm(form.copy(difficulty = level))
                            difficultyExpanded = false
                        }
                    )
                }
            }
        }

        /* ================= PASSING GRADE ================= */
        TextField(
            value = form.passingGrade,
            onValueChange = {
                viewModel.updateForm(form.copy(passingGrade = it))
            },
            label = { Text("Passing Grade") },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        PublishDateField(
            valueMillis = form.publishAtMillis,
            label = "Publish Date",
            onDateSelected = {
                viewModel.updateForm(form.copy(publishAtMillis = it))
            }
        )

        PublishDateField(
            valueMillis = form.finishAtMillis,
            label = "Finish Date",
            onDateSelected = {
                viewModel.updateForm(form.copy(finishAtMillis = it))
            }
        )



        /* ================= BANNER ================= */
        Button(
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = { imagePicker.launch("image/*") }
        ) {
            Text(
                if (form.bannerUri == null) "Pick Quiz Banner"
                else "Change Quiz Banner"
            )
        }

        /* ================= SUBMIT ================= */
        Button(
            onClick = {
                viewModel.submit(authorId) { quizId ->
                        onNavigateToAddQuestions(
                            quizId,
                            viewModel.form.value.totalQuestions.toIntOrNull() ?: 0
                        )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (viewModel.isEditMode)
                    "Save Changes"
                else
                    "Create Quiz"
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishDateField(
    valueMillis: Long?,
    label: String = "Publish Date",
    onDateSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = valueMillis
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onDateSelected(it)
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    TextField(
        value = formatDate(valueMillis),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pick date"
                )
            }
        },
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

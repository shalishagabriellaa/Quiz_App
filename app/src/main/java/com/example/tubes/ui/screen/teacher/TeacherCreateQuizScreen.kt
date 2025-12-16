package com.example.tubes.ui.screen.teacher

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    val validation by viewModel.validation.collectAsState()

    val categories = listOf("Math", "Science", "History", "Programming")
    val difficulties = listOf("easy", "medium", "hard", "extreme")

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateForm(form.copy(bannerUri = it))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        /* ================= TITLE ================= */
        TextField(
            value = form.title,
            onValueChange = {
                viewModel.updateForm(form.copy(title = it))
            },
            label = { Text("Quiz Title") },
            isError = validation.titleError != null,
            supportingText = {
                validation.titleError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        /* ================= DESCRIPTION ================= */
        TextField(
            value = form.description,
            onValueChange = {
                viewModel.updateForm(form.copy(description = it))
            },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        /* ================= CATEGORY ================= */
        DropdownField(
            label = "Category",
            value = form.categoryName,
            options = categories,
            isError = validation.categoryError,
            onSelect = {
                viewModel.updateForm(
                    form.copy(
                        categoryName = it,
                        categoryId = it.lowercase()
                    )
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        /* ================= DURATION ================= */
        NumberField(
            label = "Duration (minutes)",
            value = form.durationMinutes,
            error = validation.durationError
        ) {
            viewModel.updateForm(form.copy(durationMinutes = it))
        }

        Spacer(Modifier.height(12.dp))

        /* ================= TOTAL QUESTIONS ================= */
        NumberField(
            label = "Total Questions",
            value = form.totalQuestions,
            error = validation.totalQuestionsError
        ) {
            viewModel.updateForm(form.copy(totalQuestions = it))
        }

        Spacer(Modifier.height(12.dp))

        /* ================= DIFFICULTY ================= */
        DropdownField(
            label = "Difficulty",
            value = form.difficulty,
            options = difficulties,
            isError = validation.difficultyError,
            displayMapper = { it.replaceFirstChar(Char::uppercase) },
            onSelect = {
                viewModel.updateForm(form.copy(difficulty = it))
            }
        )

        Spacer(Modifier.height(12.dp))

        /* ================= PASSING GRADE ================= */
        NumberField(
            label = "Passing Grade",
            value = form.passingGrade,
            error = validation.passingGradeError
        ) {
            viewModel.updateForm(form.copy(passingGrade = it))
        }

        Spacer(Modifier.height(12.dp))

        /* ================= DATES ================= */
        PublishDateField(
            label = "Publish Date",
            valueMillis = form.publishAtMillis,
            error = validation.publishDateError
        ) {
            viewModel.updateForm(form.copy(publishAtMillis = it))
        }

        Spacer(Modifier.height(12.dp))

        PublishDateField(
            label = "Finish Date",
            valueMillis = form.finishAtMillis,
            error = validation.finishDateError
        ) {
            viewModel.updateForm(form.copy(finishAtMillis = it))
        }

        Spacer(Modifier.height(16.dp))

        /* ================= BANNER ================= */
        Button(onClick = { imagePicker.launch("image/*") }) {
            Text(if (form.bannerUri == null) "Pick Quiz Banner" else "Change Quiz Banner")
        }

        Spacer(Modifier.height(24.dp))

        /* ================= SUBMIT ================= */
        Button(
            onClick = {
                viewModel.submit(authorId) { quizId ->
                    onNavigateToAddQuestions(
                        quizId,
                        form.totalQuestions.toIntOrNull() ?: 0
                    )
                }
            },
            enabled = validation.isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.isEditMode) "Save Changes" else "Create Quiz")
        }
    }
}

@Composable
fun NumberField(
    label: String,
    value: String,
    error: String?,
    onChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = {
            if (it.all(Char::isDigit)) onChange(it)
        },
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    isError: String?,
    displayMapper: (String) -> String = { it },
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        TextField(
            value = displayMapper(value),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            isError = isError != null,
            supportingText = {
                isError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(expanded, { expanded = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(displayMapper(it)) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishDateField(
    label: String,
    valueMillis: Long?,
    error: String?,
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
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let(onDateSelected)
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    TextField(
        value = valueMillis?.let { formatDate(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


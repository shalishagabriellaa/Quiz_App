package com.example.tubes.ui.screen.teacher

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.viewmodel.TeacherCreateQuizViewModel
import java.util.Date
import java.util.Locale

private val TopBarColor = Color(0xFF252A57)
private val PageBgTop = Color(0xFFF6F2FF)
private val PageBgBottom = Color(0xFFF2F4FF)
private val FieldBg = Color.White
private val FieldStroke = Color(0xFFE8E8F3)
private val PrimaryBtn = Color(0xFF3E4396)
private val MutedText = Color(0xFF7A7F9A)

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
    onNavigateToAddQuestions: (String, Int) -> Unit,
    onBack: () -> Unit = {}
) {
    val form by viewModel.form.collectAsState()
    val validation by viewModel.validation.collectAsState()

    val categories = listOf("Math", "Science", "History", "Programming")
    val difficulties = listOf("easy", "medium", "hard", "extreme")

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateForm(form.copy(bannerUri = it)) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor),
                title = {
                    Text(
                        text = if (viewModel.isEditMode) "Edit Quiz" else "Create New Quiz",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
//                actions = {
//                    IconButton(onClick = { /* UI only */ }) {
//                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
//                    }
//                    Spacer(Modifier.width(8.dp))
//                    Box(
//                        modifier = Modifier
//                            .size(34.dp)
//                            .clip(CircleShape)
//                            .background(Color.White.copy(alpha = 0.18f))
//                            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("👩🏻", fontSize = 16.sp)
//                    }
//                    Spacer(Modifier.width(10.dp))
//                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PageBgTop, PageBgBottom)))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Fill out the basic quiz information before adding questions",
                    color = MutedText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        SectionTitle("(A) Quiz Details")
                        Spacer(Modifier.height(10.dp))

                        // Quiz Title*
                        LabeledField(
                            label = "Quiz Title*",
                            error = validation.titleError
                        ) {
                            AppTextField(
                                value = form.title,
                                onValueChange = { viewModel.updateForm(form.copy(title = it)) },
                                placeholder = "Input Quiz title",
                                isError = validation.titleError != null
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Description* (tanpa descriptionError biar aman)
                        LabeledField(
                            label = "Description*",
                            error = null
                        ) {
                            AppTextField(
                                value = form.description,
                                onValueChange = { viewModel.updateForm(form.copy(description = it)) },
                                placeholder = "Write a brief description about this quiz",
                                isError = false
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Category*
                        LabeledField(
                            label = "Category*",
                            error = validation.categoryError
                        ) {
                            AppDropdownField(
                                value = form.categoryName,
                                options = categories,
                                placeholder = "Select category",
                                isError = validation.categoryError != null,
                                onSelect = {
                                    viewModel.updateForm(
                                        form.copy(
                                            categoryName = it,
                                            categoryId = it.lowercase()
                                        )
                                    )
                                }
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Duration*
                        LabeledField(
                            label = "Duration (Minute)*",
                            error = validation.durationError
                        ) {
                            AppNumberField(
                                value = form.durationMinutes,
                                onValueChange = { viewModel.updateForm(form.copy(durationMinutes = it)) },
                                placeholder = "eg. 30 minutes",
                                isError = validation.durationError != null
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Total Questions*
                        LabeledField(
                            label = "Total Questions*",
                            error = validation.totalQuestionsError
                        ) {
                            AppNumberField(
                                value = form.totalQuestions,
                                onValueChange = { viewModel.updateForm(form.copy(totalQuestions = it)) },
                                placeholder = "Input total questions in this quiz",
                                isError = validation.totalQuestionsError != null
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Difficulty*
                        LabeledField(
                            label = "Difficulty Level*",
                            error = validation.difficultyError
                        ) {
                            AppDropdownField(
                                value = form.difficulty,
                                options = difficulties,
                                placeholder = "Select difficulty level",
                                isError = validation.difficultyError != null,
                                displayMapper = { it.replaceFirstChar { ch -> ch.uppercase() } },
                                onSelect = { viewModel.updateForm(form.copy(difficulty = it)) }
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Passing Grade*
                        LabeledField(
                            label = "Passing Grade*",
                            error = validation.passingGradeError
                        ) {
                            PassingGradeSlider(
                                valueText = form.passingGrade,
                                onValueChange = { newValue ->
                                    viewModel.updateForm(form.copy(passingGrade = newValue))
                                }
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        SectionTitle("(B) Quiz Settings")
                        Spacer(Modifier.height(10.dp))

                        // Publish Date*
                        LabeledField(
                            label = "Schedule Publish*",
                            error = validation.publishDateError
                        ) {
                            AppDateField(
                                valueMillis = form.publishAtMillis,
                                placeholder = "Select date publish",
                                isError = validation.publishDateError != null,
                                onDateSelected = { viewModel.updateForm(form.copy(publishAtMillis = it)) }
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Finish Date*
                        LabeledField(
                            label = "Finish Date*",
                            error = validation.finishDateError
                        ) {
                            AppDateField(
                                valueMillis = form.finishAtMillis,
                                placeholder = "Select finish date",
                                isError = validation.finishDateError != null,
                                onDateSelected = { viewModel.updateForm(form.copy(finishAtMillis = it)) }
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Banner* (tanpa bannerError biar aman)
                        LabeledField(
                            label = "Quiz Thumbnail*",
                            error = null
                        ) {
                            UploadField(
                                text = if (form.bannerUri == null) "Upload banner (png)" else "Change banner",
                                onClick = { imagePicker.launch("image/*") }
                            )
                        }

                        Spacer(Modifier.height(18.dp))

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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBtn,
                                disabledContainerColor = PrimaryBtn.copy(alpha = 0.45f)
                            )
                        ) {
                            Text(
                                text = "Save & Continue",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* =========================
   UI HELPERS
   ========================= */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF14162B),
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 6.dp)
    )
}

@Composable
private fun LabeledField(
    label: String,
    error: String?,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF14162B)
        )
        Spacer(Modifier.height(6.dp))
        content()
        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = MutedText, fontSize = 12.sp) },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            disabledContainerColor = FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PrimaryBtn
        )
    )
}

@Composable
private fun AppNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean
) {
    TextField(
        value = value,
        onValueChange = { new ->
            if (new.all(Char::isDigit)) onValueChange(new)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = MutedText, fontSize = 12.sp) },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            disabledContainerColor = FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PrimaryBtn
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDropdownField(
    value: String,
    options: List<String>,
    placeholder: String,
    isError: Boolean,
    displayMapper: (String) -> String = { it },
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = if (value.isBlank()) "" else displayMapper(value),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            placeholder = { Text(placeholder, color = MutedText, fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                disabledContainerColor = FieldBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryBtn
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(displayMapper(opt)) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDateField(
    valueMillis: Long?,
    placeholder: String,
    isError: Boolean,
    onDateSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = valueMillis)

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                        showDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    TextField(
        value = if (valueMillis == null) "" else formatDate(valueMillis),
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = MutedText, fontSize = 12.sp) },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
            }
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            disabledContainerColor = FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PrimaryBtn
        )
    )
}

@Composable
private fun UploadField(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(FieldBg)
            .border(1.dp, FieldStroke, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = MutedText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Default.Upload, contentDescription = "Upload", tint = PrimaryBtn)
        }
    }
}

@Composable
private fun PassingGradeSlider(
    valueText: String,
    onValueChange: (String) -> Unit
) {
    val initial = valueText.toIntOrNull()?.coerceIn(0, 100) ?: 80
    var sliderValue by remember(valueText) { mutableStateOf(initial.toFloat()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = sliderValue.toInt().toString(),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14162B),
            fontSize = 14.sp
        )

        Slider(
            value = sliderValue,
            onValueChange = { v ->
                sliderValue = v
                onValueChange(v.toInt().toString())
            },
            valueRange = 0f..100f
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("0", color = MutedText, fontSize = 11.sp)
            Spacer(Modifier.weight(1f))
            Text("100", color = MutedText, fontSize = 11.sp)
        }
    }
}

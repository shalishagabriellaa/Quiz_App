package com.example.tubes.ui.screen.setting

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusRequester
import coil.compose.AsyncImage
import com.example.tubes.viewmodel.PersonalInfoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // local editable fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }

    // sync backend -> local fields (kalau backend berubah)
    LaunchedEffect(
        uiState.userName,
        uiState.userEmail,
        uiState.userPhone,
        uiState.birthDate,
        uiState.gender,
        uiState.avatarUrl
    ) {
        name = uiState.userName
        email = uiState.userEmail
        phone = uiState.userPhone
        birthDate = uiState.birthDate
        gender = uiState.gender
        avatarUrl = uiState.avatarUrl
    }

    // load first time
    LaunchedEffect(Unit) { viewModel.loadUserData() }

    // focus order
    val focusName = remember { FocusRequester() }
    val focusEmail = remember { FocusRequester() }
    val focusPhone = remember { FocusRequester() }

    // Image picker
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it) } // pastikan function ini ADA di viewModel
    }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it) }
        viewModel.clearMessages()
    }

    // ===== Date Picker state =====
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) birthDate = millisToDdMmYyyy(millis)
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ===== Gender dropdown =====
    val genderOptions = listOf("Male", "Female", "Prefer not to say")
    var genderExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Personal Info", fontSize = 20.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                // ===== Avatar =====
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = avatarUrl.ifEmpty { "https://via.placeholder.com/200" },
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.25f)),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5B67CA))
                            .clickable { pickImageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Change avatar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // ===== Fields (rapi: label kiri + field kanan) =====
                InfoRowField(
                    icon = Icons.Filled.Person,
                    label = "Name",
                    value = name,
                    onValueChange = { name = it },
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    focusRequester = focusName,
                    onNext = { focusEmail.requestFocus() }
                )

                Spacer(Modifier.height(12.dp))

                InfoRowField(
                    icon = Icons.Filled.Email,
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }, // ✅ email bisa diedit
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    focusRequester = focusEmail,
                    onNext = { focusPhone.requestFocus() },
                    trailing = {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Verified",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                InfoRowField(
                    icon = Icons.Filled.Phone,
                    label = "Phone",
                    value = phone,
                    placeholder = "Enter phone number",
                    onValueChange = { phone = it },
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                    focusRequester = focusPhone,
                    onNext = { focusManager.clearFocus(); showDatePicker = true }
                )

                Spacer(Modifier.height(12.dp))

                // ===== Birth Date (klik -> date picker) =====
                InfoRowClickableField(
                    icon = Icons.Filled.DateRange,
                    label = "Birth Date",
                    value = birthDate,
                    placeholder = "Select date",
                    onClick = {
                        focusManager.clearFocus()
                        showDatePicker = true
                    }
                )

                Spacer(Modifier.height(12.dp))

                // ===== Gender dropdown =====
                InfoRowDropdownField(
                    icon = Icons.Filled.Wc,
                    label = "Gender",
                    value = gender,
                    placeholder = "Select gender",
                    expanded = genderExpanded,
                    options = genderOptions,
                    onExpandedChange = { genderExpanded = it },
                    onSelect = { selected ->
                        gender = selected
                        genderExpanded = false
                    }
                )

                Spacer(Modifier.height(22.dp))

                // ===== Save Button =====
                Button(
                    onClick = {
                        viewModel.updateUserProfile(
                            name = name,
                            email = email,
                            phone = phone,
                            birthDate = birthDate,
                            gender = gender
                        )
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B67CA)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text("Save", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

/* =========================
   COMPONENTS (ROW STYLE)
   ========================= */

@Composable
private fun InfoRowField(
    icon: ImageVector,
    label: String,
    value: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    focusRequester: FocusRequester? = null,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder, color = Color.LightGray) },
            modifier = Modifier
                .weight(1f)
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { onNext?.invoke() ?: focusManager.moveFocus(FocusDirection.Down) },
                onDone = { onDone?.invoke(); focusManager.clearFocus() }
            ),
            trailingIcon = trailing,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }

    Divider(color = Color.LightGray)
}

@Composable
private fun InfoRowClickableField(
    icon: ImageVector,
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                enabled = false, // ✅ biar klik masuk ke Box
                readOnly = true,
                singleLine = true,
                placeholder = { Text(placeholder, color = Color.LightGray) },
                trailingIcon = {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick date", tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledTextColor = LocalContentColor.current,
                    disabledPlaceholderColor = Color.LightGray,
                    disabledTrailingIconColor = Color.Gray
                )
            )
        }
    }

    Divider(color = Color.LightGray)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoRowDropdownField(
    icon: ImageVector,
    label: String,
    value: String,
    placeholder: String,
    expanded: Boolean,
    options: List<String>,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(!expanded) },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                placeholder = { Text(placeholder, color = Color.LightGray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = { onSelect(opt) }
                    )
                }
            }
        }
    }

    Divider(color = Color.LightGray)
}

/* =========================
   HELPERS
   ========================= */

private fun millisToDdMmYyyy(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

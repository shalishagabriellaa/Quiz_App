package com.example.tubes.ui.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.viewmodel.ChangePasswordViewModel

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    onBack: () -> Unit = {},
    onLoggedOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    // Focus order (Enter/Next -> next field)
    val frCurrent = remember { FocusRequester() }
    val frNew = remember { FocusRequester() }
    val frConfirm = remember { FocusRequester() }

    fun isStrongPassword(pw: String): Boolean {
        if (pw.length < 8) return false
        val hasUpper = pw.any { it.isUpperCase() }
        val hasDigit = pw.any { it.isDigit() }
        return hasUpper && hasDigit
    }

    val currentError: String? =
        if (currentPassword.isBlank()) "Current password is required." else null

    val newError: String? =
        when {
            newPassword.isBlank() -> "New password is required."
            newPassword.length < 8 -> "Must be at least 8 characters."
            !newPassword.any { it.isUpperCase() } -> "Must include at least 1 uppercase letter (A–Z)."
            !newPassword.any { it.isDigit() } -> "Must include at least 1 number (0–9)."
            else -> null
        }

    val confirmError: String? =
        when {
            confirmPassword.isBlank() -> "Please confirm your new password."
            newPassword != confirmPassword -> "Passwords do not match."
            else -> null
        }

    val isValid =
        currentError == null &&
                newError == null &&
                confirmError == null

    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar feedback
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it) }
        viewModel.clearMessages()
    }

    // Auto logout trigger
    LaunchedEffect(uiState.shouldLogout) {
        if (uiState.shouldLogout) {
            viewModel.consumeLogoutFlag()
            onLoggedOut()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Change Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp)
        ) {
            // Current Password
            Text("Current Password", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(frCurrent),
                enabled = !uiState.isLoading,
                singleLine = true, // ✅ Enter won't make newline
                visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Icon(
                            if (showCurrent) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = Color.Gray
                        )
                    }
                },
                isError = currentPassword.isNotEmpty() && currentError != null,
                supportingText = {
                    val msg = if (currentPassword.isNotEmpty()) currentError else null
                    if (msg != null) Text(msg)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { frNew.requestFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            // New Password
            Text("New Password", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(frNew),
                enabled = !uiState.isLoading,
                singleLine = true,
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(
                            if (showNew) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = Color.Gray
                        )
                    }
                },
                isError = newPassword.isNotEmpty() && newError != null,
                supportingText = {
                    val msg = if (newPassword.isNotEmpty()) newError else null
                    if (msg != null) Text(msg)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { frConfirm.requestFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Confirm New Password
            Text("Confirm New Password", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(frConfirm),
                enabled = !uiState.isLoading,
                singleLine = true,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            if (showConfirm) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = Color.Gray
                        )
                    }
                },
                isError = confirmPassword.isNotEmpty() && confirmError != null,
                supportingText = {
                    val msg = if (confirmPassword.isNotEmpty()) confirmError else null
                    if (msg != null) Text(msg)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isValid && !uiState.isLoading) {
                            viewModel.updatePassword(currentPassword, newPassword)
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Password rules: 8+ chars, at least 1 uppercase letter and 1 number.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { viewModel.updatePassword(currentPassword, newPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isValid && !uiState.isLoading,
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
                Text("Update Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

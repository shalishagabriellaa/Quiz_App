package com.example.tubes.ui.screen

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tubes.R
import com.example.tubes.auth.GoogleAuthHelper
import com.example.tubes.ui.components.SuccessfulPopup
import com.example.tubes.ui.theme.TubesTheme
import kotlinx.coroutines.delay
import com.example.tubes.viewmodel.AuthState
import com.example.tubes.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    onCreateAccount: (String, String, String) -> Unit,
    onSignInClick: () -> Unit,
    onNavigateAfterSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,

    ) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity
    val authState by viewModel.authState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(idToken) // Google register/login auto handled
            }
        } catch (e: Exception) {
            Log.e("GoogleRegister", "Failed: ${e.message}")
        }
    }
    // state popup
    var showSuccess by remember { mutableStateOf(false) }

    // Saat popup ON, auto lanjut setelah delay (simulasi proses)
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(1800) // ganti sesuai durasi prosesmu
            onNavigateAfterSuccess()
            showSuccess = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ===== LAYER KONTEN YANG DIBLUR KETIKA POPUP MUNCUL =====
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(if (showSuccess) 14.dp else 0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 140.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Create your Account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Fill in your information to get started",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(24.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Full Name", color = Color.Gray) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = Color.Gray) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = Color.Gray) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_password),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                                ),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirm Password", color = Color.Gray) },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_password),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (confirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                                ),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(Modifier.height(24.dp))

                // Button Create Account
                Button(
                    onClick = {
                        onCreateAccount(fullName, email, password)
                        showSuccess = true
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Account", color = Color.White, fontSize = 16.sp)
                }

                Spacer(Modifier.height(16.dp))

                // Divider "Or"
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Divider(modifier = Modifier.weight(1f), color = Color.White)
                    Text("  Or  ", color = Color.White)
                    Divider(modifier = Modifier.weight(1f), color = Color.White)
                }

                Spacer(Modifier.height(16.dp))

                // Login via Google
                OutlinedButton(
                    onClick = {
                        val client = GoogleAuthHelper.getClient(activity)
                        googleLauncher.launch(client.signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray
                    ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Continue with Google", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Already have an account?", color = Color.White, textAlign = TextAlign.Center)
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = onSignInClick) {
                        Text("Sign In")
                    }
                }
            }
        }

        // Scrim gelap di belakang popup
        if (showSuccess) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
        }

        // POPUP
        if (showSuccess) {
            SuccessfulPopup()
        }
        LaunchedEffect(authState) {
            if (authState is AuthState.Success) {
                onRegisterSuccess()
            }
        }
    }
}

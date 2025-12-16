package com.example.tubes.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tubes.R // Pastikan import R sesuai package project Anda
import com.example.tubes.viewmodel.ForgotPasswordState
import com.example.tubes.viewmodel.ForgotPasswordViewModel

// Definisi Warna sesuai Desain (Dark Blue Theme)
private val DarkBackground = Color(0xFF1E2146)
private val InputBackground = Color(0xFF2E325A)
private val PrimaryPurple = Color(0xFF5B61B9)
private val TextGray = Color(0xFFB0B0C0)

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle State Effects (Toast untuk feedback user)
    LaunchedEffect(state) {
        when (state) {
            is ForgotPasswordState.Success -> {
                Toast.makeText(context, "Link reset password telah dikirim ke email!", Toast.LENGTH_LONG).show()
            }
            is ForgotPasswordState.Error -> {
                Toast.makeText(context, (state as ForgotPasswordState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- Elemen Dekorasi Background (Lingkaran di pojok) ---
        BackgroundDecorations()

        // --- Konten Utama ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Scrollable jika layar kecil
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Tombol Back (Kiri Atas)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackToLogin) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Judul Besar
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. Ilustrasi (login.png)
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "Lock Illustration",
                modifier = Modifier
                    .size(250.dp) // Ukuran disesuaikan
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Sub-Judul Tengah
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Deskripsi
            Text(
                text = "Please write your email to receive a confirmation code to set a new password",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGray,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 6. Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = TextGray) },
                placeholder = { Text("greysia18@gmail.com", color = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = Color.Transparent, // Border transparan saat tidak fokus (clean look)
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = PrimaryPurple
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Tombol Confirm Mail
            Button(
                onClick = { viewModel.sendResetEmail(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Tombol tinggi sesuai desain
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = state !is ForgotPasswordState.Loading
            ) {
                if (state is ForgotPasswordState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Confirm Mail",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // Pesan Error text di bawah tombol (Optional, sebagai tambahan Toast)
            if (state is ForgotPasswordState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (state as ForgotPasswordState.Error).message,
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Komponen Hiasan Background (Lingkaran Gradasi di pojok kiri bawah)
@Composable
fun BoxScope.BackgroundDecorations() {
    Box(
        modifier = Modifier
            .size(150.dp)
            .align(Alignment.BottomStart)
            .offset(x = (-40).dp, y = 40.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE0E0FF).copy(alpha = 0.1f), Color.Transparent)
                )
            )
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .align(Alignment.BottomStart)
            .offset(x = 20.dp, y = 60.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
                )
            )
    )
}
package com.example.tubes.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    com.example.tubes.ui.theme.TubesTheme {
        RegisterScreen(
            onCreateAccount = { _, _, _ -> },
            onSignInClick = {}
        )
    }
}

@Composable
fun RegisterScreen(
    onCreateAccount: (String, String, String) -> Unit, // Callback untuk aksi daftar
    onGoogleLogin: () -> Unit = {},
    onSignInClick: () -> Unit // Callback ke halaman login
) {
    // 🎯 State untuk input form
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State untuk toggle visibility password
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // 🖼️ Background image
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 🧱 Konten utama
        Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // ✅ biar bisa digulir ke bawah
                        .padding(horizontal = 24.dp, vertical = 140.dp), // posisi tetap seperti sebelumnya
                    horizontalAlignment = Alignment.Start
                ) {

            Spacer(modifier = Modifier.height(20.dp))

            // 📝 Judul halaman
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

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Input Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = {
                    Text(
                        "Full Name",
                        color = Color.Gray
                    )
                }, // 🔹 teks placeholder abu-abu
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user), // 🔹 ikon profil di kiri
                        contentDescription = "User Icon",
                        tint = Color.Gray // warna ikon abu-abu
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // 🔹 background putih saat fokus
                    unfocusedContainerColor = Color.White,    // 🔹 background putih saat tidak fokus
                    focusedBorderColor = Color.Transparent,   // hilangkan border
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black,                // cursor hitam
                    focusedTextColor = Color.Black,           // teks hitam
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.Gray) }, // 🔹 teks placeholder abu-abu
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email), // 🔹 ikon profil di kiri
                        contentDescription = "Email Icon",
                        tint = Color.Gray // warna ikon abu-abu
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // 🔹 background putih saat fokus
                    unfocusedContainerColor = Color.White,    // 🔹 background putih saat tidak fokus
                    focusedBorderColor = Color.Transparent,   // hilangkan border
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black,                // cursor hitam
                    focusedTextColor = Color.Black,           // teks hitam
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Input Password
            var passwordVisible by remember { mutableStateOf(false) } // state untuk visibilitas password

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) }, // placeholder abu-abu
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_password), // 🔹 ikon gembok di kiri
                        contentDescription = "Password Icon",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                            ),
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                            tint = Color.Gray
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // background putih saat fokus
                    unfocusedContainerColor = Color.White,    // background putih saat tidak fokus
                    focusedBorderColor = Color.Transparent,   // tanpa border
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black,                // cursor hitam
                    focusedTextColor = Color.Black,           // teks hitam
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Confirm Password
            var confirmPasswordVisible by remember { mutableStateOf(false) } // state untuk visibilitas konfirmasi password

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text(
                        "Confirm Password",
                        color = Color.Gray
                    )
                }, // placeholder abu-abu
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_password), // ikon gembok sama seperti password
                        contentDescription = "Confirm Password Icon",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (confirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                            ),
                            contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password",
                            tint = Color.Gray
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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

            Spacer(modifier = Modifier.height(24.dp))

            // 🔵 Tombol Create Account
            Button(
                onClick = { onCreateAccount(fullName, email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Account", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔸 Divider "Or"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.White)
                Text("  Or  ", color = Color.White)
                Divider(modifier = Modifier.weight(1f), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Tombol Login via Google
            OutlinedButton(
                onClick = { onGoogleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,        // 🔹 background putih
                    contentColor = Color.Gray            // 🔹 warna teks & icon abu-abu
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue with Google",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold         // 🔹 bikin teks bold
                )
            }

//            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Tombol Login via Facebook
//            OutlinedButton(
//                onClick = { /* TODO: Tambahkan login Facebook */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(52.dp),
//                shape = RoundedCornerShape(12.dp)
//            ) {
////                Icon(
////                    painter = painterResource(id = R.drawable.ic_facebook),
////                    contentDescription = "Facebook"
////                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Continue with Facebook")
//            }

            Spacer(modifier = Modifier.height(24.dp))

            var isHovered by remember { mutableStateOf(false) }
            val interactionSource = remember { MutableInteractionSource() }

// 🔹 Navigasi ke Register (untuk halaman Sign In)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Sign In",
                    color = if (isHovered) Color(0xFF357ABD) else Color(0xFF4A90E2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {

                    }
                )
            }
        }

    }
}

package com.example.tubes.ui.screen

// 🔹 Import Compose dan Material3
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    showSystemUi = true, // biar tampil seperti di HP (status bar, dll)
    name = "Login Screen Preview"
)

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> },
    onGoogleLogin: () -> Unit = {},
    onFacebookLogin: () -> Unit = {}
) {
    // 🔹 State untuk email & password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 🔹 Gunakan background dari drawable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A083A)) // warna dominan biru gelap
    ) {
        Image(
            painter = painterResource(id = R.drawable.background), // background.png
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 Konten utama
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            // 🔹 Judul
            Text(
                text = "Sign in to your\nAccount",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                textAlign = TextAlign.Left
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Deskripsi kecil
            Text(
                text = "Enter your email and password to sign in",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 Field Email dengan background putih dan ikon profil
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.Gray) }, // 🔹 teks placeholder abu-abu
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

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Field Password dengan ikon mata show/hide
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

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Baris: Remember me dan Forgot Password
            var rememberMe by remember { mutableStateOf(false) }
            val interactionSource = remember { MutableInteractionSource() }
            var isHovered by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Remember me (klik seluruh baris)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4A90E2),
                            checkmarkColor = Color.White,
                            uncheckedColor = Color.White
                        )
                    )
                    Text(
                        text = "Remember me",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // 🖱️ Forgot Password (efek hover dengan InteractionSource)
                Text(
                    text = "Forgot Password ?",
                    color = if (isHovered) Color(0xFF73A9FF) else Color(0xFF4A90E2),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .hoverable( // butuh import foundation
                            interactionSource = interactionSource,
                            enabled = true
                        )
                        .clickable { onForgotPassword() }
                )
            }

// 🔹 Deteksi perubahan hover state (gunakan rememberUpdatedState)
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is androidx.compose.foundation.interaction.HoverInteraction.Enter -> isHovered = true
                        is androidx.compose.foundation.interaction.HoverInteraction.Exit -> isHovered = false
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Tombol Login
            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign In", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Garis pemisah "Or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.weight(1f))
                Text("  Or  ", color = Color.White  )
                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Tombol Google
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

            // 🔹 Tombol Facebook
//            OutlinedButton(
//                onClick = { onFacebookLogin() },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_facebook),
//                    contentDescription = "Facebook",
//                    tint = Color.Unspecified
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Continue with Facebook", color = Color.White)
//            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don’t have an account?",
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Sign Up",
                    color = if (isHovered) Color(0xFF357ABD) else Color(0xFF4A90E2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        // TODO: Ganti aksi sesuai kebutuhan, misalnya navigasi ke RegisterScreen
                        onSignUp()
                    }
                )
            }

        }
    }
}

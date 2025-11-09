package com.example.tubes.ui.screen

// 🔹 Import Compose dan Material3
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.tubes.auth.GoogleAuthHelper
import com.example.tubes.viewmodel.AuthViewModel
import com.example.tubes.viewmodel.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onForgotPassword: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> },
    onSignUp: () -> Unit = {},
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // ⚠️ cukup satu, jangan duplikasi
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(idToken)
            }
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Failed: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A083A))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp), // 24dp kiri & kanan
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // 🔙 Panah back sejajar kiri dengan teks
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.Start)
                    .offset(x = (-8).dp)
                    .size(36.dp)
                    .padding(bottom = 12.dp) // jarak ke teks
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

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

            Text(
                text = "Enter your email and password to sign in",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.Gray) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email Icon",
                        tint = Color.Gray
                    )
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

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_password),
                        contentDescription = "Password Icon",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
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
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Remember + Forgot
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

                Text(
                    text = "Forgot Password ?",
                    color = if (isHovered) Color(0xFF73A9FF) else Color(0xFF4A90E2),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .hoverable(interactionSource = interactionSource, enabled = true)
                        .clickable { onForgotPassword() }
                )
            }

            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { i ->
                    isHovered = i is androidx.compose.foundation.interaction.HoverInteraction.Enter
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.weight(1f))
                Text("  Or  ", color = Color.White)
                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    val client = GoogleAuthHelper.getClient(activity)
                    googleLauncher.launch(client.signInIntent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue with Google",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    color = Color(0xFF4A90E2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUp() }
                )
            }
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    onLoginSuccess()
                }
            }
        }
    }
}

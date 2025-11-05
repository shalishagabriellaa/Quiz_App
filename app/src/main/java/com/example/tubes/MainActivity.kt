package com.example.tubes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

import com.example.tubes.ui.screen.SplashScreen
import com.example.tubes.ui.screen.LoginScreen
import com.example.tubes.ui.screen.RegisterScreen
import com.example.tubes.ui.screen.home.HomeScreen

import com.example.tubes.ui.theme.TubesTheme

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    enum class Screen { SPLASH, LOGIN, REGISTER, HOME }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TubesTheme {
                var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

                when (currentScreen) {
                    Screen.SPLASH -> SplashScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN }
                    )

                    Screen.LOGIN -> LoginScreen(
                        onForgotPassword = { /* TODO */ },
                        onLogin = { email, password ->
                            // TODO: Validasi login (API/Firebase)
                            // Jika sukses:
                            currentScreen = Screen.HOME
                        },
                        onSignUp = { currentScreen = Screen.REGISTER },
                        onGoogleLogin = { /* TODO */ },
                        onBack = { currentScreen = Screen.SPLASH }
                    )

                    Screen.REGISTER -> RegisterScreen(
                        onCreateAccount = { fullName, email, password ->
                            // TODO: proses create account (API/Firebase)
                            // Popup akan muncul dari dalam RegisterScreen
                        },
                        onGoogleLogin = { /* TODO */ },
                        onSignInClick = { currentScreen = Screen.LOGIN },
                        onNavigateAfterSuccess = {
                            // Setelah popup selesai (auto delay di RegisterScreen)
                            currentScreen = Screen.LOGIN // atau ke HOME
                        }
                    )

                    Screen.HOME -> HomeScreen(
                        onNavHome = { /* stay */ },
                        onNavQuizzes = { /* TODO: navigate */ },
                        onNavQR = { /* TODO */ },
                        onNavLeaderboard = { /* TODO */ },
                        onNavProfile = { /* TODO */ }
                    )

                }
            }
        }
    }
}

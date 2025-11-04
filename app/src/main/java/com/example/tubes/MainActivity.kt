package com.example.tubes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

import com.example.tubes.ui.screen.SplashScreen
import com.example.tubes.ui.screen.LoginScreen

import com.example.tubes.ui.theme.TubesTheme

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase (jika diperlukan)
        // FirebaseApp.initializeApp(this)

        setContent {
            TubesTheme {
                // State untuk mengontrol screen mana yang ditampilkan
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onNavigateToLogin = {
                            showSplash = false
                        }
                    )
                } else {
                    LoginScreen()
                }
            }
        }
    }
}

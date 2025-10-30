package com.example.tubes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tubes.ui.screen.LoginScreen
import com.example.tubes.ui.theme.TubesTheme
import android.util.Log // Alam -> Untuk Logging
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore // Alam -> Untuk konek database

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🚀 Hapus setContentView(R.layout.activity_main)
        // dan gunakan Jetpack Compose untuk menampilkan LoginScreen
        setContent {
            TubesTheme {
                LoginScreen(
                    onForgotPassword = {
                        // TODO: navigasi ke halaman Forgot Password
                    },
                    onSignUp = {
                        // TODO: navigasi ke halaman Sign Up
                    },
                    onLogin = { email, password ->
                        // TODO: tambahkan logika login (misal cek Firebase)
                    }
                )
            }
        }
    }
}

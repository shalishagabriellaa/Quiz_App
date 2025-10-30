package com.example.tubes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.tubes.ui.screen.LoginScreen
import com.example.tubes.ui.screen.RegisterScreen

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
                RegisterScreen(
                    onCreateAccount = { fullName, email, password ->
                        // TODO: logika daftar ke Firebase misalnya
                        Log.d("Register", "Registering user: $fullName, $email")
                    },
                    onSignInClick = {
                        // TODO: kembali ke LoginScreen
                        Log.d("Navigation", "Go to Login Screen")
                    }
                )
            }
        }

    }
}

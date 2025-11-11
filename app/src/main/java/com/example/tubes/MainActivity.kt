package com.example.tubes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

import com.example.tubes.ui.screen.SplashScreen
import com.example.tubes.ui.screen.LoginScreen
import com.example.tubes.ui.screen.RegisterScreen
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.ui.screen.CategoryScreen
import com.example.tubes.ui.screen.CategorySpecifyScreen
import com.example.tubes.ui.screen.TestInformationScreen
import com.example.tubes.ui.screen.QuizScreen
import com.example.tubes.ui.screen.AnswerExplanationScreen

import com.example.tubes.ui.theme.TubesTheme

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    AnswerExplanationScreen() // ← diganti ke screen yang kamu mau
                }
            }
        }
    }
}

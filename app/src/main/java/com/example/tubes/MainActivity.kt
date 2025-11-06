package com.example.tubes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.ui.theme.TubesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TubesTheme {
                HomeScreen()
            }
        }
    }
}

package com.example.tubes

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tubes.data.AuthState
import com.example.tubes.data.repository.AuthRepositoryImpl
import com.example.tubes.ui.screen.SplashScreen
import com.example.tubes.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(AuthRepositoryImpl()) as T
            }
        }
    )

    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    // ✅ Root nav: dibuat sekali, bukan remount StudentNavigation berkali-kali
    NavHost(
        navController = navController,
        startDestination = "app_splash"
    ) {

        composable("app_splash") {
            var hasNavigated by rememberSaveable { mutableStateOf(false) }

            SplashScreen(
                onAnimationFinished = {
                    if (hasNavigated) return@SplashScreen
                    hasNavigated = true

                    val target = when (val s = authState) {
                        is AuthState.Success -> when (s.role) {
                            "admin" -> "admin_root"
                            "author" -> "teacher_root"
                            else -> "student_root"
                        }
                        else -> "student_root"
                    }

                    navController.navigate(target) {
                        popUpTo("app_splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ✅ ROOT: student flow (punya login/register/main, dll)
        composable("student_root") {
            StudentNavigation()
        }

        // ✅ ROOT: teacher flow
        composable("teacher_root") {
            TeacherAppNavigation(authViewModel = authViewModel) // pakai authViewModel yang sama
        }

        // ✅ ROOT: admin placeholder
        composable("admin_root") {
            RolePlaceholderScreen("Admin Dashboard")
        }
    }
}

@Composable
fun RolePlaceholderScreen(role: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text(text = "Navigasi untuk peran: $role")
    }
}
package com.example.tubes

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tubes.Screen
import com.example.tubes.ui.screen.*
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController, viewModel)
        }

        composable(Screen.LoginScreen.route) {
            LoginScreen(
                navController,
                viewModel,
                onLogin = { email, password ->
                    viewModel.loginUser(email, password)
                },
                onSignUp = {
                    navController.navigate(Screen.RegisterScreen.route)
                },
                onBack = {
                    navController.popBackStack()
                },
                onForgotPassword = {
                    // Panggil screen untuk lupa password
                },
                onLoginSuccess = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.LoginScreen.route)
                    }
                }
            )
        }

        // Di dalam file AppNavigation.kt
// ...
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                viewModel,
                navController,
                // Menyediakan logika untuk membuat akun, menggunakan viewModel
                onCreateAccount = { fullName, email, password ->
                    viewModel.registerUser(email, password)
                },
                // Menyediakan logika untuk pindah ke halaman Sign In
                onSignInClick = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                // Menyediakan logika untuk pindah halaman SETELAH popup sukses muncul
                onNavigateAfterSuccess = {
                    // Pindah ke Login dan hapus Register dari backstack
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                    }
                },
                // Menyediakan logika untuk pindah ke halaman Utama
                onRegisterSuccess = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.RegisterScreen.route)
                    }
                }
            )
        }

        composable(Screen.MainScreen.route) {
            HomeScreen(
                onHome = {

                },
                onProfile = {

                },
                onLeaderboard = {

                },
                onQuizzes = {

                },
                onQR = {

                },

            )
        }
// ...

    }
}

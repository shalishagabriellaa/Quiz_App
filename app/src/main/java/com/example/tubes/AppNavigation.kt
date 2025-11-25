package com.example.tubes

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tubes.data.repository.AuthRepositoryImpl
import com.example.tubes.data.repository.HomeRepositoryImpl
import com.example.tubes.ui.screen.*
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.ui.screen.home.models.toAuthorUi
import com.example.tubes.viewmodel.AuthState
import com.example.tubes.viewmodel.AuthViewModel
import com.example.tubes.viewmodel.HomeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authRepository = remember { AuthRepositoryImpl() }
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepository) as T
            }
        }
    )

    val homeRepository = remember { HomeRepositoryImpl() }
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(homeRepository) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {

        // ========== LOGIN ==========
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = { navController.navigate(Screen.RegisterScreen.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }

        // ========== REGISTER ==========
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateLogin = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                    }
                }
            )
        }

        // ========== SETTINGS ==========
        composable(Screen.SettingScreen.route) {
            SettingScreen()
        }

        // ========== PROFILE ==========
        composable(Screen.ProfileScreen.route) {
            ProfileScreen()
        }

        // ========== QUIZ (BY CODE) ==========
        composable(Screen.QuizScreen.route + "/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizScreen(quizId = quizId)
        }

        // ========== MAIN / HOME ==========
        composable("main") {
            val authState by authViewModel.authState.collectAsState()
            val state by homeViewModel.uiState.collectAsState()

            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    val uid = (authState as AuthState.Success).userId
                    Log.d("HomeScreen", "Memicu loadHome dengan UID: $uid")
                    homeViewModel.loadHome(uid)
                } else {
                    Log.w("HomeScreen", "Tidak dapat memuat data, user tidak terautentikasi.")
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                HomeScreen(
                    categories = state.categoriesUi,
                    trending = state.trendingUi,
                    topPicks = emptyList(),
                    yourQuizzes = emptyList(),
                    topAuthors = state.topAuthors.map { it.toAuthorUi() },
                    userName = state.userName,
                    avatarUrl = state.avatarUrl,
                    onHome = { },
                    onQuizzes = { },
                    onQR = { },
                    onLeaderboard = { },
                    onProfile = { navController.navigate("profile") },
                    onSettings = { navController.navigate("settings") },

                    // 🔹 ini yg bikin tombol Search bisa navigate
                    onSearchQuizCode = { code ->
                        homeViewModel.searchQuizByCode(code) { quizId ->
                            Log.d("HomeScreen", "Navigate ke quiz/$quizId")
                            navController.navigate("quiz/$quizId")
                        }
                    }
                )
            }

    }
    }
}

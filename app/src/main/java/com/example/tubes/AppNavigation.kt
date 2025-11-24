package com.example.tubes

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tubes.Screen
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
                return AuthViewModel(authRepository) as T
            }
        }
    )

    val homeRepository = remember { HomeRepositoryImpl() }
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(homeRepository) as T
            }
        }
    )


    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
        ) {

        composable(Screen.SplashScreen.route) {

            val authState by authViewModel.authState.collectAsState()
            var animationDone by remember {mutableStateOf(false)}

            LaunchedEffect(animationDone, authState) {
                if(animationDone) {
                    when(authState) {
                        is AuthState.Success -> {
                            val uid = (authState as AuthState.Success).userId
                            Log.d("UID Akan Dikirim", uid)
                            navController.navigate(Screen.MainScreen.route) {
                                popUpTo(0)
                            }
                        }
                        else -> {
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }

            SplashScreen(
                onAnimationFinished = {
                    animationDone = true
                }
            )
        }
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

// File: AppNavigation.kt

        composable(Screen.MainScreen.route) {
            // Dapatkan authState untuk mendapatkan UID
            val authState by authViewModel.authState.collectAsState()
            val state by homeViewModel.uiState.collectAsState()

            // Gunakan LaunchedEffect untuk memuat data saat layar ini pertama kali masuk komposisi
            LaunchedEffect(authState) {
                // Pastikan kita punya UID sebelum mencoba memuat data
                if (authState is AuthState.Success) {
                    val uid = (authState as AuthState.Success).userId
                    Log.d("HomeScreen", "Memicu loadHome dengan UID: $uid")
                    homeViewModel.loadHome(uid)
                } else {
                    // Opsional: Handle jika tiba-tiba user tidak login, mungkin navigasi kembali ke login
                    Log.w("HomeScreen", "Tidak dapat memuat data, user tidak terautentikasi.")
                }
            }

            // Tampilkan loading indicator jika isLoading true
            if (state.isLoading) {
                // Ganti dengan komponen loading screen Anda yang lebih baik
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Tampilkan HomeScreen hanya jika loading selesai
                HomeScreen(
                    categories = state.categoriesUi,
                    trending = state.trendingUi,
                    topPicks = emptyList(), // Ganti dengan data dari state jika sudah ada
                    yourQuizzes = emptyList(), // Ganti dengan data dari state jika sudah ada
                    topAuthors = state.topAuthors.map { it.toAuthorUi() },
                    userName = state.userName,
                    avatarUrl = state.avatarUrl,
                    onHome = {},
                    onQuizzes = {},
                    onQR = {},
                    onLeaderboard = {},
                    onProfile = {}
                )
            }
        }
    }
}

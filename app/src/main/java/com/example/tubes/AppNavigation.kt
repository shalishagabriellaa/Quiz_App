package com.example.tubes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tubes.Screen
import com.example.tubes.data.repository.AuthRepositoryImpl
import com.example.tubes.ui.screen.*
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.viewmodel.AuthState
import com.example.tubes.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val repository = remember { AuthRepositoryImpl() }
    val authViewModel = remember { AuthViewModel(repository) }

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

        composable(Screen.MainScreen.route) {
            HomeScreen()
        }
    }
}

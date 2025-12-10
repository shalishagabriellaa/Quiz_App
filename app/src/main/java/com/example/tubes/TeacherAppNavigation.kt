package com.example.tubes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tubes.data.AuthState
import com.example.tubes.ui.teacher.TeacherDashboard
import com.example.tubes.ui.teacher.components.TeacherBottomNavigation
import com.example.tubes.viewmodel.AuthViewModel

@Composable
fun TeacherAppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    val authState by authViewModel.authState.collectAsState()

    val authorId = if (authState is AuthState.Success) {
        (authState as AuthState.Success).userId
    } else {
        null
    }

    Scaffold(
        bottomBar = {
            TeacherBottomNavigation(
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ) {
            composable("dashboard") {
                TeacherDashboard(authorId = authorId)
            }
            composable("quizzes") {
                PlaceholderScreen("Quizzes")
            }
            composable("bank") {
                PlaceholderScreen("Bank")
            }
            composable("monitoring") {
                PlaceholderScreen("Monitoring")
            }
            composable("profile") {
                PlaceholderScreen("Profile")
            }
        }
    }
}

// Composable Placeholder tidak perlu diubah
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title Screen",
            color = Color.Black
        )
    }
}

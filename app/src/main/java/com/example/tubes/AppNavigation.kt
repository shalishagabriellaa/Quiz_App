package com.example.tubes

import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // <-- IMPORT BARU
import androidx.compose.runtime.remember // <-- IMPORT BARU
import androidx.compose.runtime.setValue // <-- IMPORT BARU
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tubes.data.repository.AuthRepositoryImpl
import com.example.tubes.viewmodel.AuthViewModel
import com.example.tubes.data.AuthState


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

    var isReady by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()

    if (authState !is AuthState.Idle && authState !is AuthState.Loading) {
        isReady = true
    }
    if (!isReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        when (val state = authState) {
            is AuthState.Success -> {
                Log.d("AppNavigation", "State is Stable. Role: ${state.role}. Navigating...")
                when (state.role) {
                    "admin" -> {
                        RolePlaceholderScreen("Admin Dashboard")
                    }
                    "author" -> {
                        TeacherAppNavigation()
                    }
                    else -> { // "user"
                        StudentNavigation()
                    }
                }
            }
            else -> { // AuthState.LoggedOut, AuthState.Error
                Log.d("AppNavigation", "State is Stable. User not logged in. Navigating to Login...")
                StudentNavigation()
            }
        }
    }
}

@Composable
fun RolePlaceholderScreen(role: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Navigasi untuk peran: $role")
    }
}

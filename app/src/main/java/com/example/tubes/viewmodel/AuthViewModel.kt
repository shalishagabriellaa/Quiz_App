package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object LoggedOut : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        val uid = repository.getCurrentUserId()
        _authState.value = if (uid != null) AuthState.Success(uid) else AuthState.LoggedOut
    }

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email dan Password wajib diisi")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val uid = repository.login(email, password)
                _authState.value = AuthState.Success(uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login gagal")
            }
        }
    }

    fun registerUser(fullName: String, email: String, password: String, confirm: String) {
        if (fullName.isBlank()) {
            _authState.value = AuthState.Error("Nama lengkap wajib diisi")
            return
        }
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Email wajib diisi")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password harus minimal 6 karakter")
            return
        }
        if (password != confirm) {
            _authState.value = AuthState.Error("Password tidak cocok")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val uid = repository.register(fullName, email, password)
                _authState.value = AuthState.Success(uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Register gagal")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val uid = repository.loginWithGoogle(idToken)
                Log.d("AuthViewModel", "Google login success, UID: $uid")
                _authState.value = AuthState.Success(uid)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google login error: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Google login failed")
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.LoggedOut
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

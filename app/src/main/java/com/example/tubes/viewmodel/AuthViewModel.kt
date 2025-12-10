package com.example.tubes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.AuthRepository
import com.example.tubes.data.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            try {
                val userSession = repository.getCurrentUserSession()
                if (userSession != null) {
                    val (uid, role) = userSession
                    _authState.value = AuthState.Success(uid, role)
                } else {
                    _authState.value = AuthState.LoggedOut
                }
            } catch (e: Exception) {
                _authState.value = AuthState.LoggedOut
            }
        }
    }

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email dan Password wajib diisi")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val (uid, role) = repository.login(email, password)
                _authState.value = AuthState.Success(uid, role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login gagal")
            }
        }
    }

    fun registerUser(fullName: String, email: String, password: String, confirm: String) {
        if (fullName.isBlank() || email.isBlank() || password.length < 6 || password != confirm) {
            _authState.value = AuthState.Error("Pastikan semua data terisi dengan benar.")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val (uid, role) = repository.register(fullName, email, password)
                _authState.value = AuthState.Success(uid, role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Register gagal")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val (uid, role) = repository.loginWithGoogle(idToken)
                Log.d("AuthViewModel", "Google login success, UID: $uid, Role: $role")
                _authState.value = AuthState.Success(uid, role)
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

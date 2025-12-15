package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val shouldLogout: Boolean = false // ✅ trigger logout setelah sukses
)

class ChangePasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    private fun isStrongPassword(pw: String): Boolean {
        if (pw.length < 8) return false
        val hasUpper = pw.any { it.isUpperCase() }
        val hasDigit = pw.any { it.isDigit() }
        return hasUpper && hasDigit
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            // ✅ strict validation dulu
            if (!isStrongPassword(newPassword)) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Password minimal 8 karakter dan wajib ada 1 huruf besar + 1 angka.",
                        successMessage = null,
                        shouldLogout = false
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null, shouldLogout = false) }

            try {
                val user = auth.currentUser ?: throw Exception("User not logged in")
                val email = user.email ?: throw Exception("Email not found")

                // 1) re-authenticate
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()

                // 2) update password
                user.updatePassword(newPassword).await()

                // 3) auto logout (best practice)
                auth.signOut()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Password berhasil diubah. Kamu akan diminta login ulang.",
                        shouldLogout = true
                    )
                }
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("password", ignoreCase = true) == true ->
                        "Current password is incorrect"
                    e.message?.contains("recent login", ignoreCase = true) == true ->
                        "Silakan login ulang dulu lalu coba lagi."
                    else -> e.message ?: "Failed to update password"
                }

                _uiState.update { it.copy(isLoading = false, error = msg, shouldLogout = false) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    fun consumeLogoutFlag() {
        _uiState.update { it.copy(shouldLogout = false) }
    }
}

class ChangePasswordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState =
        MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val uiState = _uiState.asStateFlow()

    fun sendResetEmail(email: String) {
        if (email.isBlank()) {
            _uiState.value =
                ForgotPasswordState.Error("Email tidak boleh kosong")
            return
        }

        _uiState.value = ForgotPasswordState.Loading

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    createResetPasswordNotification(email)

                    _uiState.value = ForgotPasswordState.Success
                } else {
                    _uiState.value = ForgotPasswordState.Error(
                        task.exception?.localizedMessage
                            ?: "Gagal mengirim email"
                    )
                }
            }
    }

    private fun createResetPasswordNotification(email: String) {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            val notification = hashMapOf(
                "userId" to user.uid,
                "title" to "Ubah Password",
                "message" to "Kami telah mengirim email untuk mengubah password akun $email.",
                "type" to "PASSWORD_RESET",
                "isRead" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("notifications").add(notification)
        }
    }
}


sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

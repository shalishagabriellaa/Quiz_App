package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.auth.GoogleAuthHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object LoggedOut : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        val currentUser = auth.currentUser
        _authState.value = if (currentUser != null) AuthState.Success(currentUser.uid)
        else AuthState.LoggedOut
    }

    fun registerUser(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success(result.user?.uid ?: "")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Register failed")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success(result.user?.uid ?: "")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val uid = GoogleAuthHelper.signInWithGoogle(idToken)
                _authState.value = AuthState.Success(uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google login failed")
            }
        }
    }

    fun registerWithGoogle(fullName: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                // Simpan data tambahan ke Firestore
                val userData = mapOf(
                    "email" to email,
                    "uid" to user?.uid
                )
                Firebase.firestore.collection("users")
                    .document(user!!.uid)
                    .set(userData)
                    .await()

                _authState.value = AuthState.Success(user.uid ?: "")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Register failed")
            }
        }
    }



    fun forceLogout() {
        auth.signOut()
        _authState.value = AuthState.LoggedOut
    }

}

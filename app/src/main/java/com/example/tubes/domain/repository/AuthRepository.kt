package com.example.tubes.domain.repository

// Di dalam file domain/repository/AuthRepository.kt

interface AuthRepository {
    suspend fun getCurrentUserSession(): Pair<String, String>?
    fun getCurrentUserId(): String?
    suspend fun login(email: String, password: String): Pair<String, String>
    suspend fun register(fullName: String, email: String, password: String): Pair<String, String>
    suspend fun loginWithGoogle(idToken: String): Pair<String, String>
    fun logout()
}

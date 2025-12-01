package com.example.tubes.domain.repository

interface AuthRepository {

    suspend fun register(
        fullName : String,
        email : String,
        password : String
    ) : String

    suspend fun login(email: String, password: String) : String

    fun getCurrentUserId() : String?

    fun logout()
    suspend fun loginWithGoogle(idToken: String): String
}
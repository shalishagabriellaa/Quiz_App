package com.example.tubes.data.repository

import com.example.tubes.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): String {

        // Firebase create account -> MUST await
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Register failed: UID null")

        val data = mapOf(
            "fullName" to fullName,
            "email" to email,
            "uid" to uid
        )

        // save to Firestore -> MUST await
        firestore.collection("users")
            .document(uid)
            .set(data)
            .await()

        return uid
    }

    override suspend fun login(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Login failed: UID null")
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override fun logout() {
        auth.signOut()
    }

    override suspend fun loginWithGoogle(idToken: String): String {
        try {

            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid ?: throw Exception("UID null")
            val userDoc = firestore.collection("users").document(uid).get().await()

            if (!userDoc.exists()) {
                val data = mapOf(
                    "uid" to uid,
                    "email" to (result.user?.email ?: ""),
                    "name" to (result.user?.displayName ?: "")
                )
                firestore.collection("users").document(uid).set(data).await()
            }

            return uid

        } catch (e: Exception) {
            throw Exception("Google Login failed: ${e.message}")
        }
    }
}

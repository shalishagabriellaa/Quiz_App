package com.example.tubes.data.repository

import com.example.tubes.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun login(email: String, password: String): String {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User ID not found after login")
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): String {
        auth.createUserWithEmailAndPassword(email, password).await()
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User ID not found after register")

        // Simpan profil user di Firestore
        val userData = mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "avatarUrl" to null
        )

        db.collection("users").document(uid).set(userData).await()
        return uid
    }

    override suspend fun loginWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("User null setelah Google login")

        val uid = user.uid

        // cek apakah sudah ada dokumen user
        val docRef = db.collection("users").document(uid)
        val snapshot = docRef.get().await()

        if (!snapshot.exists()) {
            val data = mapOf(
                "uid" to uid,
                "fullName" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "avatarUrl" to (user.photoUrl?.toString() ?: "")
            )
            docRef.set(data).await()
        }

        return uid
    }

    override fun logout() {
        auth.signOut()
    }
}

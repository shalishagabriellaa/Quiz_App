package com.example.tubes.data.repository

import com.example.tubes.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.tubes.data.model.User

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun getCurrentUserSession(): Pair<String, String>? {
        val firebaseUser = auth.currentUser ?: return null
        val uid = firebaseUser.uid
        val userDoc = db.collection("users").document(uid).get().await()
        val role = userDoc.getString("role") ?: "user"
        return Pair(uid, role)
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun login(email: String, password: String): Pair<String, String> {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid
            ?: throw IllegalStateException("User ID not found after login")

        val userDoc = db.collection("users").document(uid).get().await()
        val role = userDoc.getString("role") ?: "user"

        return Pair(uid, role)
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Pair<String, String> {
        auth.createUserWithEmailAndPassword(email, password).await()
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User ID not found after register")

        val newUser = User(
            uid = uid,
            fullName = fullName,
            email = email,
            role = "user",
            avatarUrl = null
        )

        db.collection("users").document(uid).set(newUser).await()

        return Pair(uid, "user")
    }

    override suspend fun loginWithGoogle(idToken: String): Pair<String, String> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("User null setelah Google login")

        val uid = user.uid
        var role = "user"

        val docRef = db.collection("users").document(uid)
        val snapshot = docRef.get().await()

        if (!snapshot.exists()) {
            val newUser = User(
                uid = uid,
                fullName = user.displayName ?: "",
                email = user.email ?: "",
                role = "user",
                avatarUrl = user.photoUrl?.toString()
            )
            docRef.set(newUser).await()
        } else {
            role = snapshot.getString("role") ?: "user"
        }

        return Pair(uid, role)
    }

    override fun logout() {
        auth.signOut()
    }
}

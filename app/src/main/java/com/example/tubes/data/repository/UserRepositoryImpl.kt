package com.example.tubes.data.repository

import com.example.tubes.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl {

    private val users = FirebaseFirestore.getInstance().collection("users")

    suspend fun getUser(uid: String): User {
        val doc = users.document(uid).get().await()
        return doc.toObject(User::class.java)!!
    }

    suspend fun updateAvatar(uid: String, imageUrl: String) {
        users.document(uid).update("avatarUrl", imageUrl).await() // ✅ fix
    }
}

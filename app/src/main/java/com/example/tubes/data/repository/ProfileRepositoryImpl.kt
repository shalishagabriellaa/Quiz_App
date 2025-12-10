package com.example.tubes.data.repository

import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepositoryImpl : ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getUserProfile(): User {
        val userId = auth.currentUser?.uid ?: throw Exception("User tidak login atau tidak ditemukan.")

        try {
            val document = db.collection("users").document(userId).get().await()

            return document.toObject(User::class.java)
                ?: throw Exception("Dokumen user tidak ada atau formatnya salah.")

        } catch (e: Exception) {
            throw Exception("Gagal mengambil data profil: ${e.message}")
        }
    }
}

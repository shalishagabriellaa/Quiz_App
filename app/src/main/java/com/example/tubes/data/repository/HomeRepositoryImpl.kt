package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.HomeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeRepositoryImpl : HomeRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getUser(uid: String): User? {
        // 1. Log sebelum memanggil Firestore
        Log.d("HomeRepository", "Mulai getUser | Mencoba mengambil data user dari Firestore dengan UID: $uid")

        return try {
            val documentSnapshot = db.collection("users")
                .document(uid)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                Log.d("HomeRepository", "getUser BERHASIL | Dokumen ditemukan. Data user: ${user?.fullName}")
                user
            } else {
                Log.w("HomeRepository", "getUser GAGAL | Dokumen dengan UID '$uid' tidak ditemukan di collection 'users'.")
                null // Kembalikan null karena dokumen tidak ada
            }
        } catch (e: Exception) {
            // 3. Log jika terjadi EXCEPTION saat memanggil Firestore
            Log.e("HomeRepository", "getUser EXCEPTION | Terjadi error saat mengambil data user.", e)
            null // Kembalikan null karena terjadi error
        }
    }

    override suspend fun getCategories(): List<Category> {
        return db.collection("categories")
            .get()
            .await()
            .toObjects(Category::class.java)
    }

    override suspend fun getTrendingQuizzes(): List<Quiz> {
        return db.collection("quizzes")
            .limit(10)
            .get()
            .await()
            .toObjects(Quiz::class.java)
    }

    override suspend fun getTopAuthors(): List<User> {
        return db.collection("users")
            .whereEqualTo("role", "author")
            .limit(10)
            .get()
            .await()
            .toObjects(User::class.java)
    }
}

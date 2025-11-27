package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.HomeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class HomeRepositoryImpl : HomeRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getUser(uid: String): User? {
        Log.d("HomeRepository", "Mulai getUser | UID: $uid")

        return try {
            val documentSnapshot = db.collection("users")
                .document(uid)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                Log.d("HomeRepository", "getUser BERHASIL | ${user?.fullName}")
                user
            } else {
                Log.w("HomeRepository", "getUser | Dokumen '$uid' tidak ditemukan.")
                null
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "getUser EXCEPTION", e)
            null
        }
    }

    override suspend fun getCategories(): List<Category> {
        val snapshot = db.collection("categories")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            val category = doc.toObject(Category::class.java) ?: Category()
            category.copy(id = doc.id)
        }
    }

    override suspend fun getTopAuthors(): List<User> {
        return db.collection("users")
            .whereEqualTo("role", "author")
            .limit(10)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    override suspend fun findQuizIdByCode(quizCode: String): String? {
        return try {
            val snapshot = db.collection("quizzes")
                .whereEqualTo("quizCode", quizCode)
                .limit(1)
                .get()
                .await()

            val doc = snapshot.documents.firstOrNull()
            val id = doc?.id
            Log.d("HomeRepository", "findQuizIdByCode | code=$quizCode, foundId=$id")
            id
        } catch (e: Exception) {
            Log.e("HomeRepository", "findQuizIdByCode EXCEPTION", e)
            null
        }
    }

    override suspend fun getTrendingQuizzes(): List<Quiz> {
        return try {
            val snapshot = db.collection("quizzes")
                .orderBy("attemptCount", Query.Direction.DESCENDING) // ⬅️ paling banyak dimainkan dulu
                .limit(10)
                .get()
                .await()

            snapshot.documents.map { doc ->
                val quiz = doc.toObject(Quiz::class.java) ?: Quiz()
                quiz.copy(id = doc.id) // ⬅️ penting supaya id kepakai di UI
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "getTrendingQuizzes EXCEPTION", e)
            emptyList()
        }
    }

    override suspend fun getQuizzesByCategory(categoryId: String): List<Quiz> {
        val snapshot = db.collection("quizzes")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            val quiz = doc.toObject(Quiz::class.java) ?: Quiz()
            quiz.copy(id = doc.id)
        }
    }

    // 🔹 fungsi yg dipakai TestInformationScreen
    suspend fun getQuizById(quizId: String): Quiz {
        val snapshot = db.collection("quizzes")
            .document(quizId)
            .get()
            .await()

        val quiz = snapshot.toObject(Quiz::class.java)
            ?: throw IllegalStateException("Quiz not found")

        return quiz.copy(id = snapshot.id)
    }
}

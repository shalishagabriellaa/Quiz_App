package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Category
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.data.model.UserQuizResult
import com.example.tubes.domain.repository.HomeRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

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
        val snapshot = db.collection("categories").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Category::class.java)
        }
    }

    override suspend fun getTopAuthors(): List<User> {
        return try {
            val authorsSnapshot = db.collection("users")
                .whereEqualTo("role", "author")
                .get()
                .await()

            val authorsWithScore = authorsSnapshot.documents.mapNotNull { doc ->
                val u = doc.toObject(User::class.java) ?: return@mapNotNull null
                val score = (u.followersCount) + (u.quizzesCount * 2) // ✅ pakai field baru
                Pair(u.copy(uid = doc.id), score)
            }

            authorsWithScore.sortedByDescending { it.second }
                .take(10)
                .map { it.first }
        } catch (e: Exception) {
            Log.e("HomeRepository", "getTopAuthors EXCEPTION", e)
            emptyList()
        }
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
        val snapshot = db.collection("quizzes")
            .orderBy("totalParticipants", Query.Direction.DESCENDING) // ✅ schema baru
            .limit(10)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            val quiz = doc.toObject(Quiz::class.java) ?: Quiz()
            quiz.copy(id = doc.id)
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

    // Dipakai TestInformationScreen (kalau kamu mau)
    suspend fun getQuizById(quizId: String): Quiz {
        val snapshot = db.collection("quizzes")
            .document(quizId)
            .get()
            .await()

        val quiz = snapshot.toObject(Quiz::class.java)
            ?: throw IllegalStateException("Quiz not found")

        return quiz.copy(id = snapshot.id)
    }

    override suspend fun getUserQuizResults(userId: String): List<UserQuizResult> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("quizResults")
                .orderBy("lastPlayedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(UserQuizResult::class.java)
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "getUserQuizResults EXCEPTION", e)
            emptyList()
        }
    }

    override suspend fun getLeaderboardUsers(
        limit: Int,
        weekly: Boolean
    ): List<User> {
        return try {
            val orderField = if (weekly) "weeklyScore" else "totalScore"

            // ⬅️ HAPUS whereEqualTo("role","user") supaya tidak butuh composite index
            val snapshot = db.collection("users")
                .orderBy(orderField, Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(User::class.java) ?: return@mapNotNull null

                // ⬅️ Filter hanya role "user" di sisi Kotlin
                if (user.role != "user") return@mapNotNull null

                // (Opsional) kalau mau, skip yang skornya 0
                if (orderField == "weeklyScore" && user.totalScore == 0L) {
                    // atau gunakan user.weeklyScore kalau sudah ada di model
                    // if (user.weeklyScore == 0L) return@mapNotNull null
                }

                user.copy(uid = doc.id)
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "getLeaderboardUsers EXCEPTION", e)
            emptyList()
        }
    }

}

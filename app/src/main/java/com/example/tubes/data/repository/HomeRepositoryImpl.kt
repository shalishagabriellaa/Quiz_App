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
        val snapshot = db.collection("categories")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            val category = doc.toObject(Category::class.java) ?: Category()
            category.copy(id = doc.id)
        }
    }

    // 🔝 TOP AUTHORS: berdasarkan followers + jumlah quiz yang dibuat
    override suspend fun getTopAuthors(): List<User> {
        return try {
            // Ambil semua user dengan role=author
            val authorsSnapshot = db.collection("users")
                .whereEqualTo("role", "author")
                .get()
                .await()

            // Pair (User, score)
            val authorsWithScore = authorsSnapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(User::class.java) ?: return@mapNotNull null
                val authorId = doc.id

                // Hitung followers dari subcollection "users/{authorId}/followers"
                val followersCount = try {
                    db.collection("users")
                        .document(authorId)
                        .collection("followers")
                        .get()
                        .await()
                        .size()
                } catch (e: Exception) {
                    Log.w("HomeRepository", "getTopAuthors | followers error for $authorId", e)
                    0
                }

                // Hitung jumlah quiz yang dibuat author ini
                val quizzesCount = try {
                    db.collection("quizzes")
                        .whereEqualTo("authorId", authorId)
                        .get()
                        .await()
                        .size()
                } catch (e: Exception) {
                    Log.w("HomeRepository", "getTopAuthors | quizzes error for $authorId", e)
                    0
                }

                // Bobot: followers + 2 * jumlah quiz
                val score = followersCount + (quizzesCount * 2)

                Pair(user, score)
            }

            authorsWithScore
                .sortedByDescending { it.second }
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
        return try {
            val snapshot = db.collection("quizzes")
                .orderBy("attemptCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            snapshot.documents.map { doc ->
                val quiz = doc.toObject(Quiz::class.java) ?: Quiz()
                quiz.copy(id = doc.id)
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

    // 🆕 HISTORY QUIZ USER → untuk "Your Quizzes"
    override suspend fun getUserQuizResults(userId: String): List<UserQuizResult> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("quizResults")
                .orderBy("lastPlayedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            // Group by quizId: 1 entry per quiz (ambil yang paling baru)
            val resultMap = mutableMapOf<String, UserQuizResult>()

            snapshot.documents.forEach { doc ->
                val result = doc.toObject(UserQuizResult::class.java)
                result?.let {
                    if (!resultMap.containsKey(it.quizId)) {
                        resultMap[it.quizId] = it
                    }
                }
            }

            resultMap.values.toList()
        } catch (e: Exception) {
            Log.e("HomeRepository", "getUserQuizResults EXCEPTION", e)
            emptyList()
        }
    }

    // HomeRepositoryImpl.kt

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
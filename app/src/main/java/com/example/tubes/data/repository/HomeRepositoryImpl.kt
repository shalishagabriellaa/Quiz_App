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

class HomeRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : HomeRepository {

    override suspend fun getUser(userId: String): User? {
        if (userId.isBlank()) return null
        val doc = db.collection("users").document(userId).get().await()
        if (!doc.exists()) return null
        return doc.toUserOrNull()
    }

    suspend fun getQuizByCode(quizCode: String): com.example.tubes.data.model.Quiz? {
        return try {
            val snap = db.collection("quizzes")
                .whereEqualTo("quizCode", quizCode.trim())
                .limit(1)
                .get()
                .await()

            val doc = snap.documents.firstOrNull() ?: return null
            val quiz = doc.toObject(com.example.tubes.data.model.Quiz::class.java) ?: return null
            quiz.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getCategories(): List<Category> {
        val snapshot = db.collection("categories").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Category::class.java)
        }
    }

    override suspend fun getCategoryByCategoryId(categoryId: String): Category? {
        val snap = db.collection("categories")
            .whereEqualTo("categoryId", categoryId)
            .limit(1)
            .get()
            .await()

        val doc = snap.documents.firstOrNull() ?: return null
        return doc.toObject(Category::class.java)
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
        val snap = db.collection("quizzes")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toQuizOrNull() }
    }

    override suspend fun getAttemptCountByQuizId(quizId: String): Long {
        if (quizId.isBlank()) return 0L
        val snap = db.collection("quiz_attempts")
            .whereEqualTo("quizId", quizId)
            .get()
            .await()
        return snap.size().toLong()
    }

    override suspend fun getUserQuizResults(userId: String): List<UserQuizResult> {
        return try {
            val snapshot = db.collection("quiz_attempts")
                .whereEqualTo("userId", userId)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
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

                if (user.role != "user") return@mapNotNull null

                if (orderField == "weeklyScore" && user.totalScore == 0L) {
                }

                user.copy(uid = doc.id)
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "getLeaderboardUsers EXCEPTION", e)
            emptyList()
        }
    }

    override suspend fun getQuizById(quizId: String): Quiz? {
        if (quizId.isBlank()) return null
        val doc = db.collection("quizzes").document(quizId).get().await()
        if (!doc.exists()) return null
        return doc.toQuizOrNull()
    }

}

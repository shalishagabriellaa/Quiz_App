package com.example.tubes.data.repository

import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.QuizRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuizRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuizRepository {

    override suspend fun getQuizById(quizId: String): Quiz? {
        if (quizId.isBlank()) return null
        val doc = db.collection("quizzes").document(quizId).get().await()
        if (!doc.exists()) return null
        return doc.toQuizOrNull()
    }

    override suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi> {
        if (quizId.isBlank()) return emptyList()
        val snap = db.collection("quizzes")
            .document(quizId)
            .collection("questions")
            .get()
            .await()

        // optional: urutkan berdasarkan id kalau id angka "1","2","3"
        return snap.documents
            .mapNotNull { it.toQuestionUiOrNull() }
            .sortedBy { it.id.toIntOrNull() ?: Int.MAX_VALUE }
    }

    override suspend fun getUser(userId: String): User? {
        if (userId.isBlank()) return null
        val doc = db.collection("users").document(userId).get().await()
        if (!doc.exists()) return null
        return doc.toUserOrNull()
    }

    override suspend fun saveQuizAttempt(
        userId: String?,
        quizId: String,
        quizTitle: String,
        score: Int,
        percentage: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        maxScore: Int,
        pointsEarned: Int,
        userAnswersIndex: Map<String, Int>
    ) {
        val data = hashMapOf<String, Any>(
            "quizId" to quizId,
            "quizTitle" to quizTitle,
            "score" to score,
            "percentage" to percentage,
            "correctAnswers" to correctAnswers,
            "totalQuestions" to totalQuestions,
            "maxScore" to maxScore,
            "pointsEarned" to pointsEarned,
            "userAnswersIndex" to userAnswersIndex,
            "submittedAt" to FieldValue.serverTimestamp()
        )

        if (!userId.isNullOrBlank()) data["userId"] = userId

        // ✅ kalau passed (pointsEarned > 0) tandai passedAt
        if (pointsEarned > 0) {
            data["passedAt"] = FieldValue.serverTimestamp()
        }

        db.collection("quiz_attempts").add(data).await()
    }

    override suspend fun addUserPoints(userId: String, points: Int) {
        if (userId.isBlank() || points <= 0) return
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "totalScore" to FieldValue.increment(points.toLong())
                    // kalau kamu mau weeklyScore dll bisa ditambah disini
                )
            )
            .await()
    }

    override suspend fun hasUserPassedQuiz(userId: String, quizId: String): Boolean {
        if (userId.isBlank() || quizId.isBlank()) return false

        val snap = db.collection("quiz_attempts")
            .whereEqualTo("userId", userId)
            .whereEqualTo("quizId", quizId)
            .limit(20) // ambil beberapa aja, nanti difilter di client
            .get()
            .await()

        if (snap.isEmpty) return false

        // ✅ cek di client: pernah ada pointsEarned > 0 (berarti pernah pass)
        return snap.documents.any { doc ->
            val points = doc.getLong("pointsEarned") ?: 0L
            points > 0L
        }
    }

    override suspend fun getLatestAttemptAnswers(userId: String, quizId: String): Map<String, Int> {
        if (userId.isBlank() || quizId.isBlank()) return emptyMap()

        val snap = db.collection("quiz_attempts")
            .whereEqualTo("userId", userId)
            .whereEqualTo("quizId", quizId)
            .orderBy("submittedAt")
            .limitToLast(1)
            .get()
            .await()

        val doc = snap.documents.firstOrNull() ?: return emptyMap()
        val raw = doc.get("userAnswersIndex") as? Map<*, *> ?: return emptyMap()

        return raw.entries.mapNotNull { (k, v) ->
            val key = k as? String ?: return@mapNotNull null
            val value = when (v) {
                is Long -> v.toInt()
                is Int -> v
                else -> return@mapNotNull null
            }
            key to value
        }.toMap()
    }
}

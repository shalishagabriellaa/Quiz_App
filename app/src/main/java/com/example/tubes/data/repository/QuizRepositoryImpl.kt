package com.example.tubes.data.repository

import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.QuizRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuizRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuizRepository {

    override suspend fun getQuizById(quizId: String): Quiz? {
        if (quizId.isBlank()) return null
        val doc = db.collection("quizzes").document(quizId).get().await()
        return if (doc.exists()) doc.toQuizOrNull() else null
    }

    override suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi> {
        if (quizId.isBlank()) return emptyList()

        val snap = db.collection("quizzes")
            .document(quizId)
            .collection("questions")
            .get()
            .await()

        return snap.documents
            .mapNotNull { it.toQuestionUiOrNull() }
            .sortedBy { it.id.toIntOrNull() ?: Int.MAX_VALUE }
    }

    override suspend fun getUser(userId: String): User? {
        if (userId.isBlank()) return null
        val doc = db.collection("users").document(userId).get().await()
        return if (doc.exists()) doc.toUserOrNull() else null
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
        if (userId.isNullOrBlank()) return

        // 1️⃣ Simpan attempt
        val attemptData = hashMapOf<String, Any>(
            "userId" to userId,
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

        if (pointsEarned > 0) {
            attemptData["passedAt"] = FieldValue.serverTimestamp()
        }

        db.collection("quiz_attempts").add(attemptData).await()

        // 2️⃣ Buat notifikasi (side-effect domain)
        createQuizCompletionNotifications(
            userId = userId,
            quizId = quizId,
            quizTitle = quizTitle
        )
    }

    override suspend fun addUserPoints(userId: String, points: Int) {
        if (userId.isBlank() || points <= 0) return

        db.collection("users").document(userId)
            .update("totalScore", FieldValue.increment(points.toLong()))
            .await()
    }

    override suspend fun hasUserPassedQuiz(userId: String, quizId: String): Boolean {
        if (userId.isBlank() || quizId.isBlank()) return false

        val snap = db.collection("quiz_attempts")
            .whereEqualTo("userId", userId)
            .whereEqualTo("quizId", quizId)
            .limit(20)
            .get()
            .await()

        return snap.documents.any {
            (it.getLong("pointsEarned") ?: 0L) > 0L
        }
    }

    override suspend fun getLatestAttemptAnswers(
        userId: String,
        quizId: String
    ): Map<String, Int> {
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

        return raw.mapNotNull { (k, v) ->
            val key = k as? String ?: return@mapNotNull null
            val value = when (v) {
                is Long -> v.toInt()
                is Int -> v
                else -> return@mapNotNull null
            }
            key to value
        }.toMap()
    }

    // =====================================================
    // 🔔 PRIVATE HELPERS (NOTIFICATION LOGIC)
    // =====================================================

    private suspend fun createQuizCompletionNotifications(
        userId: String,
        quizId: String,
        quizTitle: String
    ) {
        val user = getUser(userId) ?: return

        val quizDoc = db.collection("quizzes").document(quizId).get().await()
        val teacherId = quizDoc.getString("authorId") ?: return

        val now = FieldValue.serverTimestamp()

        // 🔔 Notifikasi untuk USER
        val userNotification = mapOf(
            "userId" to userId,
            "role" to "user",
            "type" to "QUIZ_COMPLETED",
            "title" to "Quiz Selesai",
            "message" to "Anda telah menyelesaikan quiz \"$quizTitle\".",
            "quizId" to quizId,
            "isRead" to false,
            "createdAt" to now
        )

        // 🔔 Notifikasi untuk TEACHER
        val teacherNotification = mapOf(
            "userId" to teacherId,
            "role" to "teacher",
            "type" to "QUIZ_SUBMITTED",
            "title" to "Quiz Diselesaikan",
            "message" to "${user.fullName} telah menyelesaikan quiz \"$quizTitle\".",
            "quizId" to quizId,
            "isRead" to false,
            "createdAt" to now
        )

        val batch = db.batch()
        batch.set(db.collection("notifications").document(), userNotification)
        batch.set(db.collection("notifications").document(), teacherNotification)
        batch.commit().await()
    }
}

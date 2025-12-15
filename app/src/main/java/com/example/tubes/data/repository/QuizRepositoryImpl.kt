package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.QuestionFirestore
import com.example.tubes.data.model.QuestionUi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

interface QuizRepository {
    suspend fun getQuizById(quizId: String): Quiz?
    suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi>

    suspend fun submitQuizResult(
        userId: String,
        quizId: String,
        userAnswers: Map<String, Int> // ✅ questionId -> selectedIndex
    )
}

class QuizRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuizRepository {

    private companion object {
        const val QUIZZES = "quizzes"
        const val QUESTIONS = "questions"
        const val USERS = "users"
        const val QUIZ_RESULTS = "quizResults"
        const val QUIZ_ATTEMPTS = "quiz_attempts"
    }

    override suspend fun getQuizById(quizId: String): Quiz? {
        return try {
            val doc = firestore.collection(QUIZZES).document(quizId).get().await()
            if (!doc.exists()) return null
            val quiz = doc.toObject(Quiz::class.java) ?: return null
            quiz.copy(id = doc.id)
        } catch (e: Exception) {
            Log.e("QuizRepository", "getQuizById error", e)
            null
        }
    }

    override suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi> {
        return try {
            val snap = firestore.collection(QUIZZES)
                .document(quizId)
                .collection(QUESTIONS)
                .get()
                .await()

            snap.documents.mapNotNull { d ->
                val q = d.toObject(QuestionFirestore::class.java) ?: return@mapNotNull null
                QuestionUi(
                    id = d.id,
                    question = q.questionText,
                    options = q.options,
                    correctAnswerIndex = q.correctAnswerIndex.toInt(),
                    explanation = q.explanation,
                    imageUrl = q.imageUrl
                )
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "getQuestionsByQuizId error", e)
            emptyList()
        }
    }

    override suspend fun submitQuizResult(
        userId: String,
        quizId: String,
        userAnswers: Map<String, Int>
    ) {
        val userRef = firestore.collection(USERS).document(userId)
        val quizRef = firestore.collection(QUIZZES).document(quizId)
        val userQuizResultRef = userRef.collection(QUIZ_RESULTS).document(quizId)
        val attemptsRef = firestore.collection(QUIZ_ATTEMPTS).document() // ✅ random id

        try {
            val questionsSnap = quizRef.collection(QUESTIONS).get().await()

            // questionId -> correctIndex
            val correctMap: Map<String, Int> = questionsSnap.documents.associate { d ->
                val idx = d.getLong("correctAnswerIndex")?.toInt() ?: 0
                d.id to idx
            }

            val totalQuestions = correctMap.size
            var correctCount = 0

            userAnswers.forEach { (questionId, selectedIdx) ->
                val correctIdx = correctMap[questionId]
                if (correctIdx != null && selectedIdx == correctIdx) correctCount++
            }

            // simple scoring: percentage * 100 (atau pakai score = correctCount)
            val score = if (totalQuestions == 0) 0 else (correctCount * 100) / totalQuestions
            val percentage = score
            val maxScore = 100

            firestore.runTransaction { tx ->
                val quizDoc = tx.get(quizRef)
                val userDoc = tx.get(userRef)
                val existingResult = tx.get(userQuizResultRef)

                // ===== update user score =====
                // (kalau weekly system kamu masih dipakai)
                val cal = Calendar.getInstance()
                val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
                val prevWeek = userDoc.getLong("weekOfYear")?.toInt() ?: 0
                val existingWeekly = userDoc.getLong("weeklyScore") ?: 0L

                val newWeekly = if (prevWeek != currentWeek) score.toLong() else existingWeekly + score

                tx.update(
                    userRef, mapOf(
                        "totalScore" to FieldValue.increment(score.toLong()),
                        "weeklyScore" to newWeekly,
                        "weekOfYear" to currentWeek
                    )
                )

                // ===== save to users/{uid}/quizResults/{quizId} =====
                val resultData = hashMapOf(
                    "quizId" to quizId,
                    "quizTitle" to (quizDoc.getString("title") ?: ""),
                    "quizBannerUrl" to quizDoc.getString("bannerUrl"),
                    "lastScore" to score.toLong(),
                    "correctAnswers" to correctCount.toLong(),
                    "totalQuestions" to totalQuestions.toLong(),
                    "lastPlayedAt" to Timestamp.now()
                )
                tx.set(userQuizResultRef, resultData)

                // ===== optional: insert quiz_attempts (schema screenshot) =====
                val attemptData = hashMapOf(
                    "userId" to userId,
                    "quizId" to quizId,
                    "quizTitle" to (quizDoc.getString("title") ?: ""),
                    "score" to score,
                    "percentage" to percentage,
                    "maxScore" to maxScore,
                    "submittedAt" to Timestamp.now()
                )
                tx.set(attemptsRef, attemptData)

                // ===== update quiz stats: participants + averageScore =====
                val oldParticipants = quizDoc.getLong("totalParticipants") ?: 0L
                val oldAvg = quizDoc.getDouble("averageScore") ?: 0.0

                val newParticipants = oldParticipants + 1
                val newAvg =
                    ((oldAvg * oldParticipants) + score.toDouble()) / newParticipants.toDouble()

                tx.update(
                    quizRef, mapOf(
                        "totalParticipants" to newParticipants,
                        "averageScore" to newAvg
                    )
                )

                // kalau kamu mau participant unik:
                // if (!existingResult.exists()) { increment participants } else jangan
                // (tapi screenshot kamu kelihatan totalParticipants ada, jadi pilih salah satu)
            }.await()

        } catch (e: Exception) {
            Log.e("QuizRepository", "submitQuizResult failed", e)
            throw e
        }
    }
}

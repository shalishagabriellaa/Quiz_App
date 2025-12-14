package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.QuestionFirestore
import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.model.toAnswerIndex
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

// Kontrak repository
interface QuizRepository {
    suspend fun getQuizById(quizId: String): Quiz?
    suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi>
    suspend fun submitQuizResult(
        userId: String,
        quizId: String,
        userAnswers: Map<String, String> // Map<QuestionID, SelectedAnswer>
    )
}

class QuizRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuizRepository {

    companion object {
        private const val TAG = "QuizRepository"
        private const val COLLECTION_QUIZZES = "quizzes"
        private const val COLLECTION_QUESTIONS = "questions"
        private const val COLLECTION_USERS = "users"
    }

    override suspend fun getQuizById(quizId: String): Quiz? {
        return try {
            Log.d(TAG, "Fetching quiz: $quizId")
            val document = firestore.collection(COLLECTION_QUIZZES)
                .document(quizId)
                .get()
                .await()

            if (document.exists()) {
                val quiz = document.toObject(Quiz::class.java)?.copy(id = document.id)
                Log.d(TAG, "Quiz found: ${quiz?.title}")
                quiz
            } else {
                Log.w(TAG, "Quiz not found: $quizId")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching quiz", e)
            null
        }
    }

    override suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi> {
        return try {
            Log.d(TAG, "Fetching questions for quiz (subcollection): $quizId")

            val querySnapshot = firestore.collection(COLLECTION_QUIZZES)
                .document(quizId)
                .collection(COLLECTION_QUESTIONS)
                .get()
                .await()

            val questions = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val question = doc.toObject(QuestionFirestore::class.java)
                    question?.let {
                        QuestionUi(
                            id = doc.id,
                            category = "",
                            question = it.text,
                            options = it.options,
                            correctAnswerIndex = it.answer.toAnswerIndex(),
                            explanation = it.explanation
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing question: ${doc.id}", e)
                    null
                }
            }
            Log.d(TAG, "Loaded ${questions.size} questions from subcollection")
            questions
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions from subcollection", e)
            emptyList()
        }
    }

    override suspend fun submitQuizResult(
        userId: String,
        quizId: String,
        userAnswers: Map<String, String>
    ) {
        val userRef = firestore.collection(COLLECTION_USERS).document(userId)
        val quizRef = firestore.collection(COLLECTION_QUIZZES).document(quizId)
        val userQuizResultRef = userRef.collection("quizResults").document(quizId)

        try {
            // ====== A. ambil jawaban benar & poin per soal dari Firestore ======
            val questionsSnapshot = quizRef.collection(COLLECTION_QUESTIONS)
                .get()
                .await()

            val correctAnswers = questionsSnapshot.documents.associate { doc ->
                val correctAnswer = doc.getString("answer")
                val points = doc.getLong("points")?.toInt() ?: 0
                doc.id to (correctAnswer to points)
            }

            // ====== B. hitung skor ======
            var calculatedScore = 0
            var correctCount = 0

            for ((questionId, selectedAnswer) in userAnswers) {
                val correctInfo = correctAnswers[questionId]
                if (correctInfo != null && selectedAnswer == correctInfo.first) {
                    calculatedScore += correctInfo.second
                    correctCount++
                }
            }
            val totalQuestions = correctAnswers.size

            // ====== C. transaksi Firestore (update user + quiz + history) ======
            firestore.runTransaction { transaction ->
                val quizDoc = transaction.get(quizRef)
                val userDoc = transaction.get(userRef)
                val existingUserResultDoc = transaction.get(userQuizResultRef)

                // ---- C1. Weekly score auto-reset per minggu ----
                val calendar = Calendar.getInstance()
                val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

                val previousWeek = userDoc.getLong("weekOfYear")?.toInt() ?: 0
                val existingWeeklyScore = userDoc.getLong("weeklyScore") ?: 0L

                val newWeeklyScore = if (previousWeek != currentWeek) {
                    // minggu berganti → reset weeklyScore, mulai dari skor quiz ini
                    calculatedScore.toLong()
                } else {
                    // minggu masih sama → tambahkan
                    existingWeeklyScore + calculatedScore
                }

                // ---- C2. update user document ----
                val userUpdate = mapOf(
                    // totalScore selalu naik (all time)
                    "totalScore" to FieldValue.increment(calculatedScore.toLong()),
                    // weeklyScore sudah dihitung manual
                    "weeklyScore" to newWeeklyScore,
                    "weekOfYear" to currentWeek
                )
                transaction.update(userRef, userUpdate)

                // ---- C3. simpan hasil di subcollection quizResults ----
                val quizResultData = hashMapOf(
                    "quizId" to quizId,
                    "quizTitle" to (quizDoc.getString("title") ?: ""),
                    "quizBannerUrl" to quizDoc.getString("bannerUrl"),
                    "lastScore" to calculatedScore,
                    "correctAnswers" to correctCount,
                    "totalQuestions" to totalQuestions,
                    "lastPlayedAt" to Timestamp.now()
                )
                transaction.set(userQuizResultRef, quizResultData)

                // ---- C4. update statistik agregat di dokumen quiz ----
                if (quizDoc.exists()) {
                    transaction.update(quizRef, "attemptCount", FieldValue.increment(1))
                    transaction.update(
                        quizRef,
                        "cumulativeScore",
                        FieldValue.increment(calculatedScore.toLong())
                    )

                    val oldAttemptCount = quizDoc.getLong("attemptCount") ?: 0L
                    val oldCumulativeScore = quizDoc.getLong("cumulativeScore") ?: 0L

                    val newAttemptCount = oldAttemptCount + 1
                    val newCumulativeScore = oldCumulativeScore + calculatedScore

                    val newAverageScore =
                        if (newAttemptCount > 0) newCumulativeScore.toDouble() / newAttemptCount
                        else 0.0

                    transaction.update(quizRef, "averageScore", newAverageScore)

                    // kalau user ini pertama kali main quiz ini → peserta +1
                    if (!existingUserResultDoc.exists()) {
                        transaction.update(
                            quizRef,
                            "totalParticipants",
                            FieldValue.increment(1)
                        )
                    }
                }
            }.await()

            Log.d(
                TAG,
                "Transaction success: Score $calculatedScore. Stats updated for quiz $quizId"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Transaction failed for submitQuizResult", e)
            throw e
        }
    }
}

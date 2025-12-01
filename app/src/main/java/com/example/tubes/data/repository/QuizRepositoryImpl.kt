package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.QuestionFirestore
import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.model.toAnswerIndex
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface QuizRepository {
    suspend fun getQuizById(quizId: String): Quiz?
    suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi>
    suspend fun submitQuizResult(userId: String, quizId: String, score: Int, totalQuestions: Int)
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

    /**
     * Mengambil data quiz berdasarkan ID
     */
    override suspend fun getQuizById(quizId: String): Quiz? {
        return try {
            Log.d(TAG, "Fetching quiz: $quizId")

            val document = firestore.collection(COLLECTION_QUIZZES)
                .document(quizId)
                .get()
                .await()

            if (document.exists()) {
                val quiz = document.toObject(Quiz::class.java)?.copy(
                    id = document.id
                )
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

    /**
     * Mengambil semua pertanyaan untuk quiz tertentu
     */
    override suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi> {
        return try {
            Log.d(TAG, "Fetching questions for quiz: $quizId")

            val querySnapshot = firestore.collection(COLLECTION_QUESTIONS)
                .whereEqualTo("quizId", quizId)
                .get()
                .await()

            val questions = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val question = doc.toObject(QuestionFirestore::class.java)
                    question?.let {
                        QuestionUi(
                            id = doc.id,
                            category = "", // akan diisi dari quiz
                            question = it.text,
                            options = it.options,
                            correctAnswerIndex = it.answer.toAnswerIndex(),
                            explanation = it.explanation      // ⬅️ PENTING
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing question: ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${questions.size} questions")
            questions
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions", e)
            emptyList()
        }
    }

    /**
     * Menyimpan hasil quiz ke user profile
     */
    // Update fungsi submitQuizResult di QuizRepositoryImpl.kt
// Tambahkan setelah update attemptCount

    override suspend fun submitQuizResult(
        userId: String,
        quizId: String,
        score: Int,
        totalQuestions: Int
    ) {
        try {
            Log.d(TAG, "Submitting quiz result for user: $userId")

            // Update total points user
            val userRef = firestore.collection(COLLECTION_USERS).document(userId)
            val userDoc = userRef.get().await()

            if (userDoc.exists()) {
                val currentScore = userDoc.getLong("totalScore")?.toInt() ?: 0
                val newScore = currentScore + score
                userRef.update("totalScore", newScore).await()
                Log.d(TAG, "User points updated: $currentScore -> $newScore")
            }

            // Update attempt count di quiz
            val quizRef = firestore.collection(COLLECTION_QUIZZES).document(quizId)
            val quizDoc = quizRef.get().await()

            var quizTitle = ""
            var quizBanner: String? = null
            var questionsCount = 0L

            if (quizDoc.exists()) {
                val currentAttempts = quizDoc.getLong("attemptCount")?.toInt() ?: 0
                quizRef.update("attemptCount", currentAttempts + 1).await()

                // Ambil info quiz untuk disimpan di result
                quizTitle = quizDoc.getString("title") ?: ""
                quizBanner = quizDoc.getString("bannerUrl")
                questionsCount = quizDoc.getLong("questionCount") ?: 0L

                Log.d(TAG, "Quiz attempt count updated")
            }

            // 🆕 Simpan hasil quiz ke subcollection user
            val quizResultData = hashMapOf(
                "quizId" to quizId,
                "quizTitle" to quizTitle,
                "quizBannerUrl" to quizBanner,
                "questionsCount" to questionsCount,
                "lastScore" to score.toLong(),
                "correctAnswers" to (score * totalQuestions / 10).toLong(),  // Hitung balik dari poin
                "totalQuestions" to totalQuestions.toLong(),
                "lastPlayedAt" to com.google.firebase.Timestamp.now()
            )

            // Simpan ke users/{userId}/quizResults/{quizId}
            // Dengan ID quizId, hasil akan ter-overwrite otomatis untuk quiz yang sama
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection("quizResults")
                .document(quizId)
                .set(quizResultData)
                .await()

            Log.d(TAG, "Quiz result saved to user profile")

        } catch (e: Exception) {
            Log.e(TAG, "Error submitting quiz result", e)
            throw e
        }
    }
}
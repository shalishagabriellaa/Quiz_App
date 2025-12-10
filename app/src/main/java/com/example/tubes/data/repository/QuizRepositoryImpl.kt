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

// --- [PERUBAHAN 1: Mengubah Kontrak/Interface] ---
// Kita tidak lagi mengirim 'score' dan 'totalQuestions' dari aplikasi.
// Aplikasi HANYA mengirimkan jawaban pengguna. Skor dihitung di server.
interface QuizRepository {
    suspend fun getQuizById(quizId: String): Quiz?
    suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi>
    suspend fun submitQuizResult(userId: String, quizId: String, userAnswers: Map<String, String>) // Map<QuestionID, SelectedAnswer>
}

class QuizRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuizRepository {

    companion object {
        private const val TAG = "QuizRepository"
        private const val COLLECTION_QUIZZES = "quizzes"
        private const val COLLECTION_QUESTIONS = "questions" // Konstanta ini tetap dipakai untuk nama subkoleksi
        private const val COLLECTION_USERS = "users"
    }

    override suspend fun getQuizById(quizId: String): Quiz? {
        // --- Tidak ada perubahan di fungsi ini ---
        return try {
            Log.d(TAG, "Fetching quiz: $quizId")
            val document = firestore.collection(COLLECTION_QUIZZES).document(quizId).get().await()
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

    // --- [PERUBAHAN 2: Mengambil Pertanyaan dari Subkoleksi] ---
    // Logika di sini diubah total untuk menargetkan subkoleksi, bukan lagi
    // memindai seluruh koleksi 'questions' utama. Ini jauh lebih cepat.
    override suspend fun getQuestionsByQuizId(quizId: String): List<QuestionUi> {
        return try {
            Log.d(TAG, "Fetching questions for quiz (subcollection): $quizId")

            // Query diubah untuk menargetkan path: /quizzes/{quizId}/questions
            val querySnapshot = firestore.collection(COLLECTION_QUIZZES) // Mulai dari quizzes
                .document(quizId)                            // Pilih dokumen kuisnya
                .collection(COLLECTION_QUESTIONS)          // Masuk ke subkoleksi questions
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
                            explanation = it.explanation,
                            // Asumsi Anda sudah menambahkan field imageUrl dan points di model QuestionFirestore
                            // imageUrl = it.imageUrl,
                            // points = it.points
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

    // --- [PERUBAHAN 3: Logika Submit Kuis Dirombak Total] ---
    // Fungsi ini sekarang menghitung skor di sisi server berdasarkan bobot poin.
    // Ini lebih aman dan fleksibel. Semua update tetap dalam satu Transaksi.
    override suspend fun submitQuizResult(
        userId: String,
        quizId: String,
        userAnswers: Map<String, String> // Terima jawaban dari user
    ) {
        val userRef = firestore.collection(COLLECTION_USERS).document(userId)
        val quizRef = firestore.collection(COLLECTION_QUIZZES).document(quizId)
        val userQuizResultRef = userRef.collection("quizResults").document(quizId)

        try {
            // Langkah A: Ambil dulu semua jawaban & poin yang benar dari Firestore
            // Ini dilakukan di luar transaksi agar tidak memberatkan transaksi.
            val questionsSnapshot = quizRef.collection(COLLECTION_QUESTIONS).get().await()
            val correctAnswers = questionsSnapshot.documents.associate { doc ->
                // Buat map: "questionId" -> Pair("jawaban_benar", poin_pertanyaan)
                doc.id to (doc.getString("answer") to (doc.getLong("points")?.toInt() ?: 0))
            }

            // Langkah B: Hitung skor di server berdasarkan jawaban pengguna
            var calculatedScore = 0
            var correctCount = 0
            for ((questionId, selectedAnswer) in userAnswers) {
                val correctAnswerInfo = correctAnswers[questionId]
                if (correctAnswerInfo != null && selectedAnswer == correctAnswerInfo.first) {
                    // Jika jawaban benar, tambahkan poinnya ke skor
                    calculatedScore += correctAnswerInfo.second
                    correctCount++
                }
            }
            val totalQuestions = correctAnswers.size

            // Langkah C: Jalankan semua update database dalam satu Transaksi
            firestore.runTransaction { transaction ->
                val quizDoc = transaction.get(quizRef)
                val existingUserResultDoc = transaction.get(userQuizResultRef)

                // C.1 - Update totalScore global user dengan skor yang baru dihitung
                transaction.update(userRef, "totalScore", FieldValue.increment(calculatedScore.toLong()))

                // C.2 - Simpan hasil detail ke subkoleksi user
                val quizResultData = hashMapOf(
                    "quizId" to quizId,
                    "quizTitle" to (quizDoc.getString("title") ?: ""),
                    "quizBannerUrl" to quizDoc.getString("bannerUrl"),
                    "lastScore" to calculatedScore, // <-- Gunakan skor hasil perhitungan server
                    "correctAnswers" to correctCount,
                    "totalQuestions" to totalQuestions,
                    "lastPlayedAt" to Timestamp.now()
                )
                transaction.set(userQuizResultRef, quizResultData)

                // C.3 - Update statistik agregat di dokumen kuis
                if (quizDoc.exists()) {
                    transaction.update(quizRef, "attemptCount", FieldValue.increment(1))
                    transaction.update(quizRef, "cumulativeScore", FieldValue.increment(calculatedScore.toLong()))

                    val newAttemptCount = (quizDoc.getLong("attemptCount") ?: 0L) + 1
                    val newCumulativeScore = (quizDoc.getLong("cumulativeScore") ?: 0L) + calculatedScore
                    val newAverageScore = if (newAttemptCount > 0) newCumulativeScore.toDouble() / newAttemptCount else 0.0
                    transaction.update(quizRef, "averageScore", newAverageScore)

                    if (!existingUserResultDoc.exists()) {
                        transaction.update(quizRef, "totalParticipants", FieldValue.increment(1))
                    }
                }
            }.await()

            Log.d(TAG, "Transaction success: Score $calculatedScore. Stats updated for quiz $quizId")
        } catch (e: Exception) {
            Log.e(TAG, "Transaction failed for submitQuizResult", e)
            throw e
        }
    }
}

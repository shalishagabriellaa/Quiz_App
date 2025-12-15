package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.AuthorStats
import com.example.tubes.domain.repository.AuthorRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class AuthorRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthorRepository {

    companion object {
        private const val TAG = "AuthorRepositoryImpl"
        private const val COLLECTION_QUIZZES = "quizzes"
    }

    override fun getAuthorStats(authorId: String): Flow<Result<AuthorStats>> = callbackFlow {
        if (authorId.isEmpty()) {
            Log.w(TAG, "Author ID is empty. Closing flow.")
            trySend(Result.failure(IllegalArgumentException("Author ID cannot be empty.")))
            close()
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection(COLLECTION_QUIZZES)
            .whereEqualTo("authorId", authorId) // Pastikan field "authorId" ada di dokumen kuis Anda
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(TAG, "Listen failed for author stats: $authorId", error)
                    trySend(Result.failure(error)) // Kirim error ke Flow
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.w(TAG, "No quizzes found for author: $authorId")
                    trySend(Result.success(AuthorStats())) // Kirim statistik kosong jika tidak ada data
                    return@addSnapshotListener
                }

                val totalQuizzes = snapshot.size()
                var totalParticipants: Long = 0
                var cumulativeScore: Double = 0.0
                var totalAttempts: Long = 0

                for (document in snapshot.documents) {
                    totalParticipants += document.getLong("totalParticipants") ?: 0L
                    val quizAverageScore = document.getDouble("averageScore") ?: 0.0
                    val quizAttemptCount = document.getLong("attemptCount") ?: 0L
                    cumulativeScore += quizAverageScore * quizAttemptCount

                    totalAttempts += quizAttemptCount
                }

                // Hitung rata-rata skor keseluruhan berdasarkan total skor dibagi total percobaan
                val overallAverageScore = if (totalAttempts > 0) {
                    // Pastikan tidak dibagi dengan nol
                    cumulativeScore / totalAttempts
                } else {
                    0.0
                }

                val stats = AuthorStats(
                    totalQuizzes = totalQuizzes,
                    totalParticipants = totalParticipants,
                    averageQuizScore = overallAverageScore
                )

                Log.d(TAG, "Stats updated for author $authorId: $stats")
                trySend(Result.success(stats)) // Kirim data statistik yang sudah dihitung ke Flow
            }
        awaitClose { listenerRegistration.remove() }
    }
}

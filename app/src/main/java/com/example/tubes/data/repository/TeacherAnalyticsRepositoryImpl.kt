package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.TeacherQuizAnalytics
import com.example.tubes.domain.repository.TeacherAnalyticsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TeacherAnalyticsRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TeacherAnalyticsRepository {

    override suspend fun getQuizzesAnalyticsByAuthor(
        authorId: String
    ): List<TeacherQuizAnalytics> {

        return try {
            val snapshot = firestore
                .collection("quizzes")
                .whereEqualTo("authorId", authorId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                TeacherQuizAnalytics(
                    quizId = doc.id,
                    title = doc.getString("title") ?: return@mapNotNull null,
                    averageScore = doc.getDouble("averageScore") ?: 0.0,
                    totalParticipants = (doc.getLong("totalParticipants") ?: 0L).toInt()
                )
            }
        } catch (e: Exception) {
            Log.e("ANALYTICS_REPO", "Firestore error", e)
            emptyList() // 🔑 PENTING: jangan throw
        }
    }
}

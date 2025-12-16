package com.example.tubes.data.repository

import com.example.tubes.data.model.TeacherNotificationUi
import com.example.tubes.domain.repository.TeacherNotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TeacherNotificationRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : TeacherNotificationRepository {

    override suspend fun getNotificationsByUser(
        userId: String
    ): List<TeacherNotificationUi> {

        val snapshot = firestore
            .collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            TeacherNotificationUi(
                id = doc.id,
                title = doc.getString("title").orEmpty(),
                message = doc.getString("message").orEmpty(),
                type = doc.getString("type").orEmpty(),
                quizId = doc.getString("quizId"), // ✅ AMAN
                isRead = doc.getBoolean("isRead") ?: false,
                createdAtMillis =
                    doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )

        }
    }

    override suspend fun markAllAsRead(
        userId: String
    ) {
        val snapshot = firestore
            .collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        val batch = firestore.batch()

        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "isRead", true)
        }

        batch.commit().await()
    }

    override suspend fun getUnreadCount(
        userId: String
    ): Int {
        val snapshot = firestore
            .collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        return snapshot.size()
    }
}

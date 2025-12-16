package com.example.tubes.data.model

import com.example.tubes.domain.repository.TeacherNotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TeacherNotificationRepositoryImpl(
    private val firestore: FirebaseFirestore
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
                quizId = doc.getString("quizId"),
                isRead = doc.getBoolean("isRead") ?: false,
                createdAtMillis =
                    doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        }
    }

    override suspend fun markAsRead(
        notificationId: String
    ) {
        firestore.collection("notifications")
            .document(notificationId)
            .update("isRead", true)
            .await()
    }
}

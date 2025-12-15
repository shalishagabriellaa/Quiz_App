package com.example.tubes.data.repository

import com.example.tubes.data.model.RecentQuizActivity
import com.example.tubes.data.model.TeacherProfileStats
import com.example.tubes.data.model.TeacherProfileUiState
import com.example.tubes.data.model.TeacherUserProfile
import com.example.tubes.domain.repository.TeacherProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TeacherProfileRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TeacherProfileRepository {

    override suspend fun getUserProfile(
        authorId: String
    ): TeacherUserProfile {

        val doc = firestore
            .collection("users")
            .document(authorId)
            .get()
            .await()

        return TeacherUserProfile(
            fullName = doc.getString("fullName") ?: "Educator",
            avatarUrl = doc.getString("avatarUrl")
        )
    }

    override suspend fun getQuizzesByAuthor(
        authorId: String
    ): List<RecentQuizActivity> {

        val snapshot = firestore
            .collection("quizzes")
            .whereEqualTo("authorId", authorId)
            .get()
            .await()

        return snapshot.documents.map {
            RecentQuizActivity(
                quizId = it.id,
                title = it.getString("title") ?: "",
                bannerUrl = it.getString("bannerUrl"),
                totalParticipants =
                    (it.getLong("totalParticipants") ?: 0L).toInt(),
                publishAt =
                    it.getTimestamp("publishAt")?.seconds ?: 0
            )
        }
    }
}

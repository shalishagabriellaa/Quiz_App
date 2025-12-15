package com.example.tubes.data.repository

import com.example.tubes.data.model.TeacherCreateQuizFirestore
import com.example.tubes.data.model.TeacherCreateQuizUi
import com.example.tubes.data.model.TeacherQuizUi
import com.example.tubes.data.model.TeacherQuizUserFirestore
import com.example.tubes.domain.repository.TeacherQuizRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class TeacherQuizRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TeacherQuizRepository {
    override suspend fun getTeacherQuizzes(authorId: String): List<TeacherQuizUi> {
        val snapshot = firestore
            .collection("quizzes")
            .whereEqualTo("authorId", authorId)
            .get()
            .await()
        val userDoc = firestore
            .collection("users")
            .document(authorId)
            .get()
            .await()

        val user = userDoc.toObject(TeacherQuizUserFirestore::class.java)

        return snapshot.documents.map { doc ->
            val model = doc.toObject(TeacherQuizFirestore::class.java)

            TeacherQuizUi(
                id = doc.id,
                title = model?.title.orEmpty(),
                totalQuestions = model?.totalQuestions ?: 0,
                status = model?.status.orEmpty(),
                totalParticipants = model?.totalParticipants ?: 0,
                averageScore = model?.averageScore ?: 0.0,
                createdAtMillis = model?.createdAt?.toDate()?.time,
                bannerUrl = model?.bannerUrl,
                authorName = user?.fullName.orEmpty(),
                authorAvatarUrl = user?.avatarUrl
            )
        }
    }

    override suspend fun deleteTeacherQuizzes(quizId: String) {
        firestore.collection("quizzes")
            .document(quizId)
            .delete()
            .await()
    }

    override suspend fun createQuiz(
        authorId: String,
        quiz: TeacherCreateQuizUi,
        bannerUrl: String?
    ): String {

        val publishAt = quiz.publishAtMillis?.let { Timestamp(Date(it)) }
        val finishAt = quiz.finishAtMillis?.let { Timestamp(Date(it)) }

        val status = when {
            publishAt == null -> "draft"
            publishAt.toDate().after(Date()) -> "draft"
            finishAt != null && finishAt.toDate().before(Date()) -> "completed"
            else -> "active"
        }

        val data = TeacherCreateQuizFirestore(
            title = quiz.title,
            description = quiz.description,
            categoryId = quiz.categoryId,
            categoryName = quiz.categoryName,
            authorId = authorId,

            durationMinutes = quiz.durationMinutes.toIntOrNull() ?: 0,
            totalQuestions = quiz.totalQuestions.toIntOrNull() ?: 0,
            difficulty = quiz.difficulty,
            passingGrade = quiz.passingGrade.toIntOrNull() ?: 0,

            publishAt = publishAt,
            finishAt = finishAt,
            status = status,
            bannerUrl = bannerUrl
        )

        val docRef = firestore.collection("quizzes")
            .add(data)
            .await()

        return docRef.id
    }
    override suspend fun getById(quizId: String): TeacherCreateQuizUi {
        val snapshot = firestore
            .collection("quizzes")
            .document(quizId)
            .get()
            .await()

        if (!snapshot.exists()) {
            throw IllegalStateException("Quiz not found")
        }

        return TeacherCreateQuizUi(
            title = snapshot.getString("title") ?: "",
            description = snapshot.getString("description") ?: "",
            categoryId = snapshot.getString("categoryId") ?: "",
            categoryName = snapshot.getString("categoryName") ?: "",

            durationMinutes = (snapshot.getLong("durationMinutes") ?: 0L).toString(),

            // 🔑 INI YANG BIKIN AMAN
            totalQuestions = (snapshot.getLong("totalQuestions") ?: 0L).toString(),

            difficulty = snapshot.getString("difficulty") ?: "",
            passingGrade = (snapshot.getLong("passingGrade") ?: 0L).toString(),

            publishAtMillis = snapshot.getTimestamp("publishAt")?.toDate()?.time,
            finishAtMillis = snapshot.getTimestamp("finishAt")?.toDate()?.time
        )
    }


    override suspend fun updateQuiz(
        quizId: String,
        quiz: TeacherCreateQuizUi,
        bannerUrl: String?
    ) {
        firestore.collection("quizzes")
            .document(quizId)
            .update(
                mapOf(
                    "title" to quiz.title,
                    "description" to quiz.description,
                    "categoryId" to quiz.categoryId,
                    "categoryName" to quiz.categoryName,
                    "difficulty" to quiz.difficulty,

                    // 🔑 FIX DI SINI (PAKAI KURUNG)
                    "durationMinutes" to (quiz.durationMinutes.toIntOrNull() ?: 0),
                    "totalQuestions" to (quiz.totalQuestions.toIntOrNull() ?: 0),
                    "passingGrade" to (quiz.passingGrade.toIntOrNull() ?: 0),

                    "bannerUrl" to bannerUrl,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
    }
}
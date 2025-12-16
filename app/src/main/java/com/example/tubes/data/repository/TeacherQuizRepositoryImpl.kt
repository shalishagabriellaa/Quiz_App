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

    // =========================
    // 🔔 HELPER NOTIFICATION
    // =========================
    private suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        message: String,
        quizId: String
    ) {
        firestore.collection("notifications")
            .add(
                mapOf(
                    "userId" to userId,
                    "role" to "author",
                    "type" to type,
                    "title" to title,
                    "message" to message,
                    "quizId" to quizId,
                    "isRead" to false,
                    "createdAt" to Timestamp.now()
                )
            )
            .await()
    }

    // =========================
    // GET QUIZZES
    // =========================
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

        val user =
            userDoc.toObject(TeacherQuizUserFirestore::class.java)

        return snapshot.documents.map { doc ->

            val model =
                doc.toObject(TeacherQuizFirestore::class.java)

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

    // =========================
    // CREATE QUIZ + NOTIF
    // =========================
    override suspend fun createQuiz(
        authorId: String,
        quiz: TeacherCreateQuizUi,
        bannerUrl: String?
    ): String {

        val publishAt =
            quiz.publishAtMillis?.let { Timestamp(Date(it)) }

        val finishAt =
            quiz.finishAtMillis?.let { Timestamp(Date(it)) }

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

        val docRef = firestore
            .collection("quizzes")
            .add(data)
            .await()

        // 🔔 NOTIFICATION — QUIZ CREATED
        createNotification(
            userId = authorId,
            type = "QUIZ_CREATED",
            title = "Quiz berhasil dibuat",
            message = "Quiz \"${quiz.title}\" berhasil dibuat",
            quizId = docRef.id
        )

        return docRef.id
    }

    // =========================
    // DELETE QUIZ + NOTIF
    // =========================
    override suspend fun deleteTeacherQuizzes(quizId: String) {

        val snapshot = firestore
            .collection("quizzes")
            .document(quizId)
            .get()
            .await()

        if (!snapshot.exists()) return

        val title =
            snapshot.getString("title") ?: "Quiz"

        val authorId =
            snapshot.getString("authorId") ?: return

        firestore.collection("quizzes")
            .document(quizId)
            .delete()
            .await()

        // 🔔 NOTIFICATION — QUIZ DELETED
        createNotification(
            userId = authorId,
            type = "QUIZ_DELETED",
            title = "Quiz dihapus",
            message = "Quiz \"$title\" telah dihapus",
            quizId = quizId
        )
    }

    // =========================
    // GET QUIZ BY ID
    // =========================
    override suspend fun getById(
        quizId: String
    ): TeacherCreateQuizUi {

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
            durationMinutes =
                (snapshot.getLong("durationMinutes") ?: 0L).toString(),
            totalQuestions =
                (snapshot.getLong("totalQuestions") ?: 0L).toString(),
            difficulty = snapshot.getString("difficulty") ?: "",
            passingGrade =
                (snapshot.getLong("passingGrade") ?: 0L).toString(),
            publishAtMillis =
                snapshot.getTimestamp("publishAt")?.toDate()?.time,
            finishAtMillis =
                snapshot.getTimestamp("finishAt")?.toDate()?.time
        )
    }

    // =========================
    // UPDATE QUIZ
    // =========================
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
                    "durationMinutes" to
                            (quiz.durationMinutes.toIntOrNull() ?: 0),
                    "totalQuestions" to
                            (quiz.totalQuestions.toIntOrNull() ?: 0),
                    "passingGrade" to
                            (quiz.passingGrade.toIntOrNull() ?: 0),
                    "bannerUrl" to bannerUrl,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
    }

    override suspend fun generateQuizQr(
        quizId: String,
        expiredAtMillis: Long
    ): String {

        val quizCode = (100000..999999).random().toString()

        firestore.collection("quizzes")
            .document(quizId)
            .update(
                mapOf(
                    "quizCode" to quizCode,
                    "qrExpiredAt" to Timestamp(Date(expiredAtMillis)),
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()

        return quizCode
    }
}
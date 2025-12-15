package com.example.tubes.data.repository

import android.util.Log
import com.example.tubes.data.model.Difficulty
import com.example.tubes.data.model.TeacherQuestionBank
import com.example.tubes.domain.repository.TeacherQuestionBankRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TeacherQuestionBankRepositoryImpl(
    private val db: FirebaseFirestore
) : TeacherQuestionBankRepository {

    override suspend fun getTeacherQuestionBank(
        authorId: String,
        difficulty: Difficulty?,
        limit: Int
    ): List<TeacherQuestionBank> {

        Log.d("BANK_DEBUG", "QUERY authorId = '$authorId'")

        var quizQuery = db.collection("quizzes")
            .whereEqualTo("authorId", authorId)

        val quizSnapshots = quizQuery.get().await()
        Log.d("BANK_DEBUG", "QUIZ COUNT = ${quizSnapshots.size()}")
        val result = mutableListOf<TeacherQuestionBank>()

        for (quizDoc in quizSnapshots.documents) {

            val quizId = quizDoc.id
            val quizTitle = quizDoc.getString("title") ?: ""
            val rawDifficulty = quizDoc.getString("difficulty")?.lowercase()

            Log.d("BANK_DEBUG", "raw difficulty='$rawDifficulty'")

            // ✅ FILTER YANG BENAR (LOWERCASE STRING)
            if (difficulty != null) {
                val selected = difficulty.name.lowercase()
                if (rawDifficulty != selected) {
                    continue
                }
            }

            val quizDifficulty = Difficulty.fromFirestore(rawDifficulty)

            val questionSnapshots = quizDoc.reference
                .collection("questions")
                .limit(limit.toLong())
                .get()
                .await()

            Log.d(
                "BANK_DEBUG",
                "Quiz ${quizDoc.id} questionCount=${questionSnapshots.size()}"
            )

            for (qDoc in questionSnapshots.documents) {
                result.add(
                    TeacherQuestionBank(
                        id = qDoc.id,
                        quizId = quizId,
                        quizTitle = quizTitle,
                        difficulty = quizDifficulty,
                        questionText = qDoc.getString("questionText") ?: "",
                        options = qDoc.get("options") as? List<String> ?: emptyList(),
                        correctAnswerIndex = (qDoc.getLong("correctAnswerIndex") ?: 0L).toInt(),
                        explanation = qDoc.getString("explanation"),
                        imageUrl = qDoc.getString("imageUrl"),
                        updatedAt = qDoc.getLong("updatedAt") ?: 0L
                    )
                )
            }
        }

        return result
            .sortedByDescending { it.updatedAt }
            .take(limit)
    }

    override suspend fun deleteTeacherQuestion(
        quizId: String,
        questionId: String
    ) {
        db.collection("quizzes")
            .document(quizId)
            .collection("questions")
            .document(questionId)
            .delete()
            .await()
    }
}

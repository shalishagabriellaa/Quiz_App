package com.example.tubes.data.repository

import com.example.tubes.data.model.TeacherQuestionFirestore
import com.example.tubes.data.model.TeacherQuestionForm
import com.example.tubes.domain.repository.TeacherQuestionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TeacherQuestionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TeacherQuestionRepository {

    override suspend fun saveQuestion(
        quizId: String,
        index: Int,
        form: TeacherQuestionForm
    ) {
        firestore.collection("quizzes")
            .document(quizId)
            .collection("questions")
            .document(index.toString())
            .set(form)
            .await()
    }

    override suspend fun getQuestion(
        quizId: String,
        index: Int
    ): TeacherQuestionForm? {
        val snapshot = firestore.collection("quizzes")
            .document(quizId)
            .collection("questions")
            .document(index.toString())
            .get()
            .await()

        return snapshot.toObject(TeacherQuestionForm::class.java)
    }
}

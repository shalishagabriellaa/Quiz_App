package com.example.tubes.data.repository

import com.example.tubes.data.model.QuestionUi
import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toQuizOrNull(): Quiz? {
    val obj = this.toObject(Quiz::class.java) ?: return null
    return obj.copy(id = this.id)
}

fun DocumentSnapshot.toUserOrNull(): User? {
    val obj = this.toObject(User::class.java) ?: return null
    return if (obj.uid.isBlank()) obj.copy(uid = this.id) else obj
}

fun DocumentSnapshot.toQuestionUiOrNull(): QuestionUi? {
    // DB kamu pakai "questionText"
    val questionText = getString("questionText") ?: return null
    val options = get("options") as? List<String> ?: emptyList()
    val correct = (getLong("correctAnswerIndex") ?: -1L).toInt()
    val explanation = getString("explanation") ?: ""
    val imageUrl = getString("imageUrl") // bisa null

    return QuestionUi(
        id = this.id,
        question = questionText,
        options = options,
        correctAnswerIndex = correct,
        explanation = explanation,
        imageUrl = imageUrl
    )
}

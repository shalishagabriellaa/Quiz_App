package com.example.tubes.data.repository

import com.example.tubes.data.model.Quiz
import com.example.tubes.data.model.User
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toQuizOrNull(): Quiz? {
    val obj = this.toObject(Quiz::class.java) ?: return null
    // Firestore tidak otomatis isi field "id" dari documentId
    return obj.copy(id = this.id)
}

fun DocumentSnapshot.toUserOrNull(): User? {
    val obj = this.toObject(User::class.java) ?: return null
    // Pastikan uid keisi (kalau di doc field uid kosong)
    return if (obj.uid.isBlank()) obj.copy(uid = this.id) else obj
}

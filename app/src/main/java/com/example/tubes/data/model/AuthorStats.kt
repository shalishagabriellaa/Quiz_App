package com.example.tubes.data.model


data class AuthorStats(
    val totalQuizzes: Int = 0,
    val totalParticipants: Long = 0, // Menggunakan Long untuk mengakomodasi angka besar
    val averageQuizScore: Double = 0.0, // Skor rata-rata keseluruhan (dalam persen)
    // val totalFollowers: Int = 0 // Anda bilang ini dikecualikan, tapi ini tempatnya
)

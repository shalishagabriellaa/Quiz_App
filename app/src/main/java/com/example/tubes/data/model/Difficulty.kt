package com.example.tubes.data.model

enum class Difficulty {
    EASY, MEDIUM, HARD, EXTREME;

    companion object {
        fun fromFirestore(raw: String?): Difficulty {
            return when (raw?.trim()?.lowercase()) {
                "easy" -> EASY
                "medium" -> MEDIUM
                "hard" -> HARD
                else -> EXTREME
            }
        }
    }
}

package com.example.tubes.util

import java.util.concurrent.TimeUnit

fun formatTimeAgo(timeMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - timeMillis).coerceAtLeast(0)

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        seconds < 5 -> "Just now"
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        weeks < 4 -> "$weeks weeks ago"
        months < 12 -> "$months months ago"
        else -> "$years years ago"
    }
}

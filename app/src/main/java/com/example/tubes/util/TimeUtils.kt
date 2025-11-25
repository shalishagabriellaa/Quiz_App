package com.example.tubes.util

import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

fun formatRelativeTime(createdAt: Timestamp?): String {
    if (createdAt == null) return "-"

    val createdMs = createdAt.toDate().time
    val nowMs = System.currentTimeMillis()
    val diffMs = nowMs - createdMs

    if (diffMs < 0) return "-" // kalau jam device ngaco

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
    val hours = TimeUnit.MILLISECONDS.toHours(diffMs)
    val days = TimeUnit.MILLISECONDS.toDays(diffMs)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        days < 30 -> "${days / 7} weeks ago"
        days < 365 -> "${days / 30} months ago"
        else -> "${days / 365} years ago"
    }
}

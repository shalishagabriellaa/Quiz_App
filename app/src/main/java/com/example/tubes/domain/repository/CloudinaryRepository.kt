package com.example.tubes.domain.repository

import android.net.Uri

interface CloudinaryRepository {
    suspend fun uploadAvatar(uri: Uri): String
}

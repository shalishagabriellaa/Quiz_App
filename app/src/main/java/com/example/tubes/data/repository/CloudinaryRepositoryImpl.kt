package com.example.tubes.data.repository

import android.net.Uri
import com.example.tubes.data.cloudinary.CloudinaryManager
import com.example.tubes.domain.repository.CloudinaryRepository

class CloudinaryRepositoryImpl(
    private val cloudinary: CloudinaryManager
) : CloudinaryRepository {

    override suspend fun uploadAvatar(uri: Uri): String {
        return cloudinary.uploadImage(uri)
    }

    override suspend fun uploadImage(uri: Uri): String {
        return cloudinary.uploadImage(uri)

    }
}

package com.example.tubes.data.cloudinary

import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CloudinaryManager(
    cloudName: String,
    apiKey: String,
    apiSecret: String
) {
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret
        )
    )
    suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val result = cloudinary.uploader().upload(uri.path, ObjectUtils.emptyMap())
        result["secure_url"].toString()
    }
}
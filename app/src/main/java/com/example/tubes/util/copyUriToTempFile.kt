package com.example.tubes.util

import android.content.Context
import android.net.Uri
import java.io.File

fun copyUriToTempFile(
    context: Context,
    uri: Uri
): File {
    val input = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Cannot open uri")

    val file = File.createTempFile(
        "upload_", ".tmp", context.cacheDir
    )

    file.outputStream().use { output ->
        input.copyTo(output)
    }

    return file
}

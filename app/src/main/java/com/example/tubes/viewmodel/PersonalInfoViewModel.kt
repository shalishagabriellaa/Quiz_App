package com.example.tubes.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.repository.ProfileRepositoryImpl
import com.example.tubes.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage

data class PersonalInfoUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val avatarUrl: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

class PersonalInfoViewModel(
    private val repository: ProfileRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalInfoUiState())
    val uiState: StateFlow<PersonalInfoUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ====== CLOUDINARY CONFIG (ISI PUNYA KAMU) ======
    private val CLOUDINARY_CLOUD_NAME = "ISI_CLOUD_NAME_KAMU"
    private val CLOUDINARY_UNSIGNED_PRESET = "ISI_UPLOAD_PRESET_UNSIGNED"
    // ===============================================

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

                val doc = db.collection("users").document(userId).get().await()
                val fullName = doc.getString("fullName").orEmpty()
                val email = doc.getString("email").orEmpty().ifEmpty { auth.currentUser?.email.orEmpty() }
                val phone = doc.getString("phone").orEmpty()
                val birthDate = doc.getString("birthDate").orEmpty()
                val gender = doc.getString("gender").orEmpty()
                val avatarUrl = doc.getString("avatarUrl").orEmpty()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = fullName,
                        userEmail = email,
                        userPhone = phone,
                        birthDate = birthDate,
                        gender = gender,
                        avatarUrl = avatarUrl
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load user data") }
            }
        }
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        birthDate: String,
        gender: String,
        email: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

                val updates = mapOf(
                    "fullName" to name,
                    "phone" to phone,
                    "birthDate" to birthDate,
                    "gender" to gender,
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                db.collection("users").document(userId).update(updates).await()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = name,
                        userPhone = phone,
                        birthDate = birthDate,
                        gender = gender,
                        successMessage = "Profile updated successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update profile"
                    )
                }
            }
        }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not logged in")

                val storageRef = FirebaseStorage.getInstance()
                    .reference
                    .child("avatars/$userId.jpg")

                // upload ke storage
                storageRef.putFile(uri).await()

                // ambil URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // simpan ke Firestore
                db.collection("users")
                    .document(userId)
                    .update("avatarUrl", downloadUrl)
                    .await()

                // update UI
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        avatarUrl = downloadUrl,
                        successMessage = "Profile photo updated"
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to upload avatar"
                    )
                }
            }
        }
    }

    private suspend fun uploadToCloudinaryUnsigned(uri: Uri): String {
        if (CLOUDINARY_CLOUD_NAME.startsWith("ISI_") || CLOUDINARY_UNSIGNED_PRESET.startsWith("ISI_")) {
            throw Exception("Cloudinary config belum diisi (cloud_name / upload_preset).")
        }

        val file = copyUriToTempFile(appContext, uri)
        val client = OkHttpClient()

        val mime = appContext.contentResolver.getType(uri) ?: "image/*"
        val fileBody = file.asRequestBody(mime.toMediaTypeOrNull())

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, fileBody)
            .addFormDataPart("upload_preset", CLOUDINARY_UNSIGNED_PRESET)
            .build()

        val url = "https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload"

        val req = Request.Builder()
            .url(url)
            .post(multipart)
            .build()

        val res = client.newCall(req).execute()
        val bodyStr = res.body?.string().orEmpty()

        if (!res.isSuccessful) {
            throw Exception("Cloudinary upload failed: ${res.code} - $bodyStr")
        }

        val json = JSONObject(bodyStr)
        val secureUrl = json.optString("secure_url")
        if (secureUrl.isBlank()) throw Exception("Cloudinary tidak mengembalikan secure_url")

        return secureUrl
    }

    private fun copyUriToTempFile(context: Context, uri: Uri): File {
        val input = context.contentResolver.openInputStream(uri) ?: throw Exception("Cannot open image")
        val tempFile = File(context.cacheDir, "avatar_${UUID.randomUUID()}.jpg")
        input.use { inp ->
            tempFile.outputStream().use { out -> inp.copyTo(out) }
        }
        return tempFile
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

class PersonalInfoViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalInfoViewModel::class.java)) {
            val repository = ProfileRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return PersonalInfoViewModel(repository, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

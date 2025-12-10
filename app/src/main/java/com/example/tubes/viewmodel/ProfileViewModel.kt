package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.User // <-- IMPORT DATA CLASS USER
import com.example.tubes.data.repository.ProfileRepositoryImpl // <-- Import implementasi langsung
import com.example.tubes.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State untuk UI Profile (ini sudah bagus, tidak perlu diubah)
data class ProfileUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String = "",
    val userPoints: Int = 0,
    val error: String? = null
)

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // State awal -> Loading
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Gunakan try-catch untuk menangani sukses dan gagal
            try {
                // Panggil repository. Jika sukses, `user` akan berisi data.
                val user: User = repository.getUserProfile()

                // Update state jika sukses
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // Akses properti dari data class User, jauh lebih aman!
                        userName = user.fullName ?: "Name Unknown",
                        userEmail = user.email,
                        userPoints = 0
                    )
                }

            } catch (e: Exception) {
                // Jika repository melempar Exception, akan ditangkap di sini.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Terjadi kesalahan yang tidak diketahui"
                    )
                }
            }
        }
    }
}

// --- FACTORY UNTUK MEMBUAT VIEWMODEL (TANPA HILT) ---
// Karena ViewModel sekarang butuh ProfileRepository di constructor-nya,
// kita perlu cara untuk membuatnya. Di sinilah Factory berperan.
class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Buat instance repository secara manual di sini
            val repository = ProfileRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

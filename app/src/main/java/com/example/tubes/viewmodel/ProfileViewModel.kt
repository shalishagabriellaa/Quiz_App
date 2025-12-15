package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.repository.ProfileRepositoryImpl
import com.example.tubes.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String = "",
    val avatarUrl: String = "",
    val totalPoints: Int = 0,
    val quizzesCompleted: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val rank: Int = 0,
    val error: String? = null
)

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val user = repository.getUserProfile()
                val playsCount = repository.getQuizAttemptsCount(user.uid)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = user.fullName ?: "Name Unknown",
                        userEmail = user.email,
                        avatarUrl = user.avatarUrl.orEmpty(),
                        totalPoints = user.totalScore.toInt(),
                        quizzesCompleted = playsCount,
//                        followersCount = user.followers.size,
//                        followingCount = user.following.size,
                        rank = 0 // TODO: Implement ranking logic if needed
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Terjadi kesalahan"
                    )
                }
            }
        }
    }

    fun followUser(targetUserId: String) {
        viewModelScope.launch {
            try {
//                repository.followUser(targetUserId)
//                loadProfile() // Refresh untuk update count
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    fun unfollowUser(targetUserId: String) {
        viewModelScope.launch {
            try {
//                repository.unfollowUser(targetUserId)
//                loadProfile() // Refresh untuk update count
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }
}

class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val repository = ProfileRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.User
import com.example.tubes.data.repository.ProfileRepositoryImpl
import com.example.tubes.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FollowersUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val error: String? = null
)

class FollowersViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FollowersUiState())
    val uiState: StateFlow<FollowersUiState> = _uiState.asStateFlow()

    fun loadFollowers(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val followers = repository.getFollowers(userId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        users = followers
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
}

class FollowersViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowersViewModel::class.java)) {
            val repository = ProfileRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return FollowersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

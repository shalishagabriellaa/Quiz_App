package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// dipakai UI juga
data class LeaderboardUser(
    val id: String,
    val name: String,
    val points: Int,
    val avatarUrl: String,
    val rank: Int
)

enum class LeaderboardTab {
    WEEKLY,
    ALL_TIME
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val users: List<LeaderboardUser> = emptyList(),
    val error: String? = null
)

// LeaderboardViewModel.kt
class LeaderboardViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    fun loadLeaderboard(tab: LeaderboardTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val weekly = (tab == LeaderboardTab.WEEKLY)
                val users = repo.getLeaderboardUsers(limit = 50, weekly = weekly)

                val leaderboardUsers = users.mapIndexed { index, user ->
                    LeaderboardUser(
                        id = user.uid,
                        name = user.fullName
//                            ?: user.fullName
                            ?: user.email.substringBefore("@"),

                        // 🔥 PENTING: weeklyScore untuk tab Weekly, totalScore untuk All Time
                        points = if (weekly) {
                            user.weeklyScore?.toInt() ?: 0   // safe null check
                        } else {
                            user.totalScore?.toInt() ?: 0
                        },

                        avatarUrl = user.avatarUrl ?: "",
                        rank = index + 1
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = leaderboardUsers,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}
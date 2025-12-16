package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.data.model.TeacherNotificationUi
import com.example.tubes.domain.repository.TeacherNotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationUiState(
    val isLoading: Boolean = true,
    val notifications: List<TeacherNotificationUi> = emptyList()
)

class TeacherNotificationViewModel(
    private val repository: TeacherNotificationRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val data =
                repository.getNotificationsByUser(userId)

            _uiState.value =
                NotificationUiState(
                    isLoading = false,
                    notifications = data
                )
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
            loadNotifications()
        }
    }
}

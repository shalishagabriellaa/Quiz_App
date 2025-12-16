package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubes.domain.repository.TeacherNotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherNotificationBadgeViewModel(
    private val repository: TeacherNotificationRepository,
    private val userId: String
) : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count = _count.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _count.value = repository.getUnreadCount(userId)
        }
    }
}

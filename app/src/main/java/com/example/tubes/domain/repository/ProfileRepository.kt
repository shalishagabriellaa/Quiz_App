package com.example.tubes.domain.repository

import com.example.tubes.data.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUserProfile(): User
}

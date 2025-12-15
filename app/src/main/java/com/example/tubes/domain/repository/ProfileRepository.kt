package com.example.tubes.domain.repository

import com.example.tubes.data.model.User

interface ProfileRepository {
    suspend fun getUserProfile(): User
    suspend fun getUserById(uid: String): User
    suspend fun getQuizAttemptsCount(uid: String): Int

    // Follow system
//    suspend fun followUser(targetUserId: String)
//    suspend fun unfollowUser(targetUserId: String)
//    suspend fun isFollowing(targetUserId: String): Boolean
//    suspend fun getFollowers(uid: String): List<User>
//    suspend fun getFollowing(uid: String): List<User>
}

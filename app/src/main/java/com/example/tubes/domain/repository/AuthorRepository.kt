package com.example.tubes.domain.repository

import com.example.tubes.data.model.AuthorStats
import kotlinx.coroutines.flow.Flow

interface AuthorRepository {
    fun getAuthorStats(authorId: String): Flow<Result<AuthorStats>>
}
package com.mehrbod.data.repository.movies

import com.mehrbod.data.repository.model.User

interface UserRepository {
    suspend fun signup(user: User): String
    suspend fun canLogin(user: User): Boolean
}

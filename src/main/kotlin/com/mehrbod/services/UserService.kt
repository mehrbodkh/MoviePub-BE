package com.mehrbod.services

import com.mehrbod.controllers.UserInfoDTO
import com.mehrbod.data.repository.model.User
import com.mehrbod.data.repository.movies.UserRepository

class UserService(
    private val usersRepository: UserRepository
) {

    suspend fun signup(user: UserInfoDTO) = usersRepository.signup(User(user.username, user.email, user.password))

    suspend fun canLogin(user: UserInfoDTO): Boolean = usersRepository.canLogin(User(user.username, user.email, user.password))
}
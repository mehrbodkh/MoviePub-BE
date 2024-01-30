package com.mehrbod.data.repository.movies

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mehrbod.data.datasource.local.LocalUserDataSource
import com.mehrbod.data.repository.model.User

class UserRepositoryImpl(
    private val localUserDataSource: LocalUserDataSource
) : UserRepository {
    override suspend fun signup(user: User): String {
        localUserDataSource.addUser(
            user.copy(
                password = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
            )
        )
        return "OK"
    }

    override suspend fun canLogin(user: User): Boolean {
        val localUser = localUserDataSource.findUser(user)

        return localUser != null && BCrypt.verifyer().verify(user.password.toCharArray(), localUser.password.toCharArray()).verified
    }
}
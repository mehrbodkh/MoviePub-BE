package com.mehrbod.data.datasource.local

import com.mehrbod.data.repository.model.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class LocalUserDataSource(
    database: Database
) {
    object UsersTable : IntIdTable(name = "users") {
        val username = varchar("username", 50).uniqueIndex()
        val email = varchar("email", 50).uniqueIndex()
        val password = text("password")
    }

    class UsersDao(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<UsersDao>(UsersTable)

        var username by UsersTable.username
        var email by UsersTable.email
        var password by UsersTable.password
    }

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun addUser(user: User) = dbQuery {
        if (UsersDao.find { UsersTable.email eq user.email }.empty()) {
            UsersDao.new {
                this.username = user.username
                this.password = user.password
                this.email = user.email
            }
        }
    }

    suspend fun findUser(user: User) = dbQuery {
        UsersDao.find { UsersTable.username eq user.username }.singleOrNull()
    }
}
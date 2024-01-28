package com.mehrbod.data.datasource.local

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class LocalGenreDataSource(
    database: Database
) {
    object GenresTable : IntIdTable(name = "genres") {
        val originalId = integer("original_id")
        val name = varchar("name", 50)
    }

    class GenreDao(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<GenreDao>(GenresTable)

        var originalId by GenresTable.originalId
        var name by GenresTable.name
    }

    init {
        transaction(database) {
            SchemaUtils.create(GenresTable)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun addGenre(id: Int, name: String) = dbQuery {
        if (GenreDao.find { GenresTable.originalId eq id }.empty()) {
            GenreDao.new {
                this.originalId = id
                this.name = name
            }
        }
    }

    suspend fun fetchGenres(): List<Pair<Int, String>> = dbQuery {
        GenreDao.all().map {
            it.originalId to it.name
        }
    }
}

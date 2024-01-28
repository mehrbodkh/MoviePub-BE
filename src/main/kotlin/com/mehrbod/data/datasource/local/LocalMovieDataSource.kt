package com.mehrbod.data.datasource.local

import com.mehrbod.data.repository.model.Movie
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class LocalMovieDataSource(
    database: Database
) {
    object MoviesTable : IntIdTable() {
        val adult = bool("adult")
        val backdropPath = varchar("backdrop_path", 256).nullable()
        val genreIds = text("genre_ids")
        val originalId = integer("original_id")
        val originalLanguage = varchar("original_language", 50)
        val originalTitle = varchar("original_title", 256)
        val overview = text("overview")
        val popularity = double("popularity")
        val posterPath = text("poster_path").nullable()
        val releaseDate = varchar("release_data", 50)
        val title = text("title")
        val video = bool("video")
        val voteAverage = double("vote_average")
        val voteCount = integer("vote_count")
    }

    class MovieDao(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<MovieDao>(MoviesTable)

        var adult by MoviesTable.adult
        var backdropPath by MoviesTable.backdropPath
        var genreIds by MoviesTable.genreIds
        var originalId by MoviesTable.originalId
        var originalLanguage by MoviesTable.originalLanguage
        var originalTitle by MoviesTable.originalTitle
        var overview by MoviesTable.overview
        var popularity by MoviesTable.popularity
        var posterPath by MoviesTable.posterPath
        var releaseDate by MoviesTable.releaseDate
        var title by MoviesTable.title
        var video by MoviesTable.video
        var voteAverage by MoviesTable.voteAverage
        var voteCount by MoviesTable.voteCount
    }

    init {
        transaction(database) {
            SchemaUtils.create(MoviesTable)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun addMovie(movie: Movie) = dbQuery {
        if (MovieDao.find { MoviesTable.originalId eq movie.id }.empty()) {
            MovieDao.new {
                this.adult = movie.adult
                this.backdropPath = movie.backdropPath
                this.genreIds = movie.genreIds.joinToString()
                this.originalId = movie.id
                this.originalLanguage = movie.originalLanguage
                this.originalTitle = movie.originalTitle
                this.overview = movie.overview
                this.popularity = movie.popularity
                this.posterPath = movie.posterPath
                this.releaseDate = movie.releaseDate
                this.title = movie.title
                this.video = movie.video
                this.voteAverage = movie.voteAverage
                this.voteCount = movie.voteCount
            }
        }
    }

    suspend fun fetchMovies(page: Int): Pair<List<Movie>, Int> = dbQuery {
        MovieDao.all().limit(20, ((page - 1) * 20).toLong()).orderBy(MoviesTable.voteAverage to SortOrder.DESC).toList()
            .map {
                Movie(
                    it.adult,
                    it.backdropPath,
                    it.genreIds.split(", ").map { it.toInt() },
                    it.originalId,
                    it.originalLanguage,
                    it.originalTitle,
                    it.overview,
                    it.popularity,
                    it.posterPath,
                    it.releaseDate,
                    it.title,
                    it.video,
                    it.voteAverage,
                    it.voteCount
                )
            } to (MovieDao.count().toInt() / 20) + 1
    }

}

package com.mehrbod.data.datasource.local

import com.mehrbod.data.repository.model.Movie
import io.ktor.util.logging.*
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
    object Movies : IntIdTable() {
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

    class Movie(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Movie>(Movies)
        var adult by Movies.adult
        var backdropPath by Movies.backdropPath
        var genreIds by Movies.genreIds
        var originalId by Movies.originalId
        var originalLanguage by Movies.originalLanguage
        var originalTitle by Movies.originalTitle
        var overview by Movies.overview
        var popularity by Movies.popularity
        var posterPath by Movies.posterPath
        var releaseDate by Movies.releaseDate
        var title by Movies.title
        var video by Movies.video
        var voteAverage by Movies.voteAverage
        var voteCount by Movies.voteCount
    }

    init {
        transaction(database) {
            SchemaUtils.create(Movies)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun addMovie(movie: com.mehrbod.data.repository.model.Movie) = dbQuery {
        if (Movie.find { Movies.originalId eq movie.id }.empty()) {
            Movie.new {
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

    suspend fun fetchMovies(page: Int): List<com.mehrbod.data.repository.model.Movie> = dbQuery {
        Movie.all().limit(20, ((page - 1) * 20).toLong()).orderBy(Movies.voteAverage to SortOrder.DESC).toList().map {
            com.mehrbod.data.repository.model.Movie(
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
        }
    }

}

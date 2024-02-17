package com.mehrbod.data.repository.movies

import com.mehrbod.data.datasource.local.LocalGenreDataSource
import com.mehrbod.data.datasource.local.LocalMovieDataSource
import com.mehrbod.data.datasource.remote.RemoteMovieDataSource
import com.mehrbod.data.repository.model.Movie
import com.mehrbod.data.repository.model.Movies
import kotlinx.coroutines.*

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localDataSource: LocalMovieDataSource,
    private val localGenreDataSource: LocalGenreDataSource,
) : MovieRepository {

    override suspend fun getTopMovies(page: Int): Movies = withContext(Dispatchers.IO) {
        var movies = localDataSource.fetchMovies(page)
        if (movies.first.isEmpty()) {
            localDataSource.addMovies(remoteDataSource.getTopMovies(page).results.map {
                Movie(
                    it.adult,
                    it.backdrop_path,
                    it.genre_ids,
                    it.id,
                    it.original_language,
                    it.original_title,
                    it.overview,
                    it.popularity,
                    it.poster_path,
                    it.release_date,
                    it.title,
                    it.video,
                    it.vote_average,
                    it.vote_count,
                )
            })
        }
        movies = localDataSource.fetchMovies(page)
        Movies(
            page = page,
            movies = movies.first,
            totalPages = movies.second
        )
    }

    override suspend fun getGenres(): List<Pair<Int, String>> {
        var result = localGenreDataSource.fetchGenres()

        if (result.isEmpty()) {
            remoteDataSource.getGenres().forEach {
                localGenreDataSource.addGenre(it.first, it.second)
            }
        }

        result = localGenreDataSource.fetchGenres()

        return result
    }

    override suspend fun loadAllTopMovies() {
        for (page in 1..458) {
            getTopMovies(page)
        }
    }
}
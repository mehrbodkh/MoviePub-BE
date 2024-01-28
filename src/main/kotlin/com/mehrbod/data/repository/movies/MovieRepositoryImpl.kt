package com.mehrbod.data.repository.movies

import com.mehrbod.data.datasource.local.LocalMovieDataSource
import com.mehrbod.data.datasource.remote.RemoteMovieDataSource
import com.mehrbod.data.repository.model.Movie
import com.mehrbod.data.repository.model.Movies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localDataSource: LocalMovieDataSource,
) : MovieRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getTopMovies(page: Int): Movies {
        var movies = localDataSource.fetchMovies(page)
        if (movies.first.isEmpty()) {
            coroutineScope.launch {
                remoteDataSource.getTopMovies(page).results.forEach {
                    localDataSource.addMovie(
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
                    )
                }
            }
        }
        movies = localDataSource.fetchMovies(page)
        return Movies(
            page = page,
            movies = movies.first,
            totalPages = movies.second
        )
    }
}
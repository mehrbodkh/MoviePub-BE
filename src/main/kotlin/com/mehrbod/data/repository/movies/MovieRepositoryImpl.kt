package com.mehrbod.data.repository.movies

import com.mehrbod.data.datasource.local.LocalMovieDataSource
import com.mehrbod.data.datasource.remote.RemoteMovieDataSource
import com.mehrbod.data.repository.model.Movies

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localDataSource: LocalMovieDataSource,
) : MovieRepository {
    override suspend fun getTopMovies(page: Int): Movies {
        val movies = localDataSource.fetchMovies(page)
        return Movies(
            page = page,
            movies = movies,
            totalPages = 457
        )
    }
}
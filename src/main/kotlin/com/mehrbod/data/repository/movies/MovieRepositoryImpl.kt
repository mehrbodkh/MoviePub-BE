package com.mehrbod.data.repository.movies

import com.mehrbod.data.datasource.RemoteMovieDataSource
import com.mehrbod.data.repository.model.Movies

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteMovieDataSource
) : MovieRepository {
    override suspend fun getTopMovies(page: Int): Movies {
        return remoteDataSource.getTopMovies(page).toMovies()
    }
}
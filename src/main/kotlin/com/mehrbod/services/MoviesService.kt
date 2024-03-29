package com.mehrbod.services

import com.mehrbod.data.repository.movies.MovieRepository

class MoviesService(
    private val repository: MovieRepository
) {

    suspend fun getTopMovies(page: Int) = repository.getTopMovies(page)
}

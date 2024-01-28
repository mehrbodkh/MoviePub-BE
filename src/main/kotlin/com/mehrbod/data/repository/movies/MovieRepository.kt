package com.mehrbod.data.repository.movies

import com.mehrbod.data.repository.model.Movies

interface MovieRepository {
    suspend fun getTopMovies(page: Int): Movies
}

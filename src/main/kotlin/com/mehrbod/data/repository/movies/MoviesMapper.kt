package com.mehrbod.data.repository.movies

import com.mehrbod.data.datasource.remote.model.MovieDTO
import com.mehrbod.data.datasource.remote.model.MoviesDTO
import com.mehrbod.data.repository.model.Movie
import com.mehrbod.data.repository.model.Movies

internal fun MoviesDTO.toMovies() = Movies(
    page = this.page,
    movies = this.results.map { it.toMovie() },
    totalPages = this.total_pages
)

private fun MovieDTO.toMovie() = Movie(
    adult = this.adult,
    backdropPath = this.backdrop_path,
    genreIds = this.genre_ids,
    id = this.id,
    originalLanguage = this.original_language,
    originalTitle = this.original_title,
    overview = this.overview,
    popularity = this.popularity,
    posterPath = this.poster_path,
    releaseDate = this.release_date,
    title = this.title,
    video = this.video,
    voteAverage = this.vote_average,
    voteCount = this.vote_count
)


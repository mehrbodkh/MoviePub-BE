package com.mehrbod.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class MoviesDTO(
    val page: Int,
    val results: List<MovieDTO>,
    val total_pages: Int,
    val total_results: Int,
)
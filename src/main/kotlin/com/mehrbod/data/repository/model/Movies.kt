package com.mehrbod.data.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class Movies(
    val page: Int,
    val movies: List<Movie>,
    val totalPages: Int,
)

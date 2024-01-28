package com.mehrbod.data.datasource.remote

import com.mehrbod.data.datasource.remote.model.MoviesDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class RemoteMovieDataSource(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3"
    }

    suspend fun getTopMovies(page: Int): MoviesDTO {
        val response: MoviesDTO = httpClient.get("$BASE_URL/movie/top_rated") {
            url {
                parameters.append("language", "en-US")
                parameters.append("page", page.toString())
                parameters.append("api_key", apiKey)
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }.body()

        return response
    }

    @Serializable
    data class Genres(
        val genres: List<Genre>
    )

    @Serializable
    data class Genre(
        val id: Int,
        val name: String,
    )

    suspend fun getGenres(): List<Pair<Int, String>> {
        return httpClient.get("$BASE_URL/genre/movie/list") {
            url {
                parameters.append("language", "en-US")
                parameters.append("api_key", apiKey)
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }.body<Genres>().genres.map { it.id to it.name }
    }
}

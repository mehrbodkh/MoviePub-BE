package com.mehrbod.di

import com.mehrbod.data.datasource.RemoteMovieDataSource
import com.mehrbod.data.repository.movies.MovieRepository
import com.mehrbod.data.repository.movies.MovieRepositoryImpl
import com.mehrbod.services.MoviesService
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val moviesModule = module {
    single<String>(named("api_key")) {
        val config: ApplicationConfig by inject()
        config.property("tmdb.api_key").getString()
    }
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            expectSuccess = true
        }
    }
    single<RemoteMovieDataSource> { RemoteMovieDataSource(get(), get(named("api_key"))) }
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single<MoviesService> { MoviesService(get()) }
}
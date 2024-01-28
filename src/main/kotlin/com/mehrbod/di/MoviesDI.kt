package com.mehrbod.di

import com.mehrbod.data.datasource.local.LocalGenreDataSource
import com.mehrbod.data.datasource.local.LocalMovieDataSource
import com.mehrbod.data.datasource.remote.RemoteMovieDataSource
import com.mehrbod.data.repository.movies.MovieRepository
import com.mehrbod.data.repository.movies.MovieRepositoryImpl
import com.mehrbod.services.MoviesService
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.koin.core.qualifier.named
import org.koin.dsl.module

val moviesModule = module {
    factory {
        val config: ApplicationConfig by inject()

        val jdbcUrl = config.property("storage.jdbcUrl").getString()
        val driverClassName = config.property("storage.driverClassName").getString()
        val user = config.property("storage.username").getString()
        val password = config.property("storage.password").getString()

        Database.connect(
            HikariDataSource().apply {
                this.driverClassName = driverClassName
                this.jdbcUrl = jdbcUrl
                this.username = user
                this.password = password

                maximumPoolSize = 3
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
        )
    }

    single<LocalMovieDataSource> {
        LocalMovieDataSource(get())
    }
    single<LocalGenreDataSource> {
        LocalGenreDataSource(get())
    }
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
    single<MovieRepository> { MovieRepositoryImpl(get(), get(), get()) }
    single<MoviesService> { MoviesService(get()) }
}
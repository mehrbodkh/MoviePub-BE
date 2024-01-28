package com.mehrbod.controllers

import com.mehrbod.services.MoviesService
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(AutoHeadResponse)

    val moviesService: MoviesService by inject()

    routing {
        route("/v1") {
            get("/movie/top_rated") {
                val page = call.request.queryParameters["page"] ?: "1"
                val result = moviesService.getTopMovies(page.toInt())
                call.respond(result)
            }
            get("/movie/genres/list") {
                call.respond(moviesService.getGenres())
            }
        }
    }
}

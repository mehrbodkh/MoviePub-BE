package com.mehrbod.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(AutoHeadResponse)
    val apiKey = environment.config.property("tmdb.api_key").getString()
    routing {
        route("/v1") {
            get {
                call.respondText("Hello World!")
            }
            get("/movie/top_rated") {
                val page = call.request.queryParameters["page"] ?: "1"
                val client = HttpClient {
                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = LogLevel.ALL
                    }
                    expectSuccess
                }
                val response: String = client.get("https://api.themoviedb.org/3/movie/top_rated") {
                    url {
                        parameters.append("language", "en-US")
                        parameters.append("page", page)
                        parameters.append("api_key", apiKey)
                    }
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                    }
                }.body()
                println(response)
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}

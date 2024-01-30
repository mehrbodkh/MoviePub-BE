package com.mehrbod.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mehrbod.services.MoviesService
import com.mehrbod.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.util.*

@Serializable
data class UserInfoDTO(
    val email: String,
    val username: String,
    val password: String,
)

fun Application.configureRouting() {
    install(AutoHeadResponse)

    val moviesService: MoviesService by inject()
    val userService: UserService by inject()

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

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
            post("/signup") {
                val requestBody = call.receive<UserInfoDTO>()
                userService.signup(requestBody)
                val token = JWT.create()
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .withClaim("username", requestBody.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000))
                    .sign(Algorithm.HMAC256(jwtSecret))
                call.respond("jwt" to token)
            }
            post("/login") {
                val requestBody = call.receive<UserInfoDTO>()
                if (userService.canLogin(requestBody)) {
                    val token = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer(jwtDomain)
                        .withClaim("username", requestBody.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000))
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond("jwt" to token)
                } else {
                    call.respondText("Incorrect username or password")
                }

            }
            authenticate {
                get("/movie/genres/list-pro") {
                    call.respond(moviesService.getGenres())
                }
            }
        }
    }
}

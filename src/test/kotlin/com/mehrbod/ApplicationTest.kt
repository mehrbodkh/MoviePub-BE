package com.mehrbod

import com.mehrbod.controllers.configureRouting
import com.mehrbod.data.datasource.remote.RemoteMovieDataSource
import com.mehrbod.di.configureKoin
import com.mehrbod.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testData() = testApplication {
        environment {
            config = MapApplicationConfig("tmdb.api_key" to "some_data")
        }
        application {
            configureSerialization()
            configureKoin()
            configureRouting()
        }
        externalServices {
            hosts(RemoteMovieDataSource.BASE_URL) {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    get("/movie/top_rated?language=en-US&page=1") {
                        call.respond("HelloWorld")
                    }
                }
            }
        }

        client.get("/v1/movie/top_rated?page=1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}

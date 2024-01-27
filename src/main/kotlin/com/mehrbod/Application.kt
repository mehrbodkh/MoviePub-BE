package com.mehrbod

import com.mehrbod.plugins.*
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.tomcat.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}

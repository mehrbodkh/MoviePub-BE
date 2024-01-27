package com.mehrbod

import com.mehrbod.controllers.configureRouting
import com.mehrbod.di.configureKoin
import com.mehrbod.plugins.*
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.tomcat.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureKoin()
    configureHTTP()
    configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}

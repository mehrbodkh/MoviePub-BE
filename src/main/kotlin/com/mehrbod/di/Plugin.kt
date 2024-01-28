package com.mehrbod.di

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


fun Application.configureKoin() {
    install(Koin) {
        modules(
            module {
                single { environment.config }
            },
            moviesModule
        )
    }
}

package com.mehrbod.di

import com.mehrbod.data.datasource.local.LocalUserDataSource
import com.mehrbod.data.repository.movies.UserRepository
import com.mehrbod.data.repository.movies.UserRepositoryImpl
import com.mehrbod.services.UserService
import org.koin.dsl.module

val usersModule = module {
    single<LocalUserDataSource> {
        LocalUserDataSource(get())
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<UserService> {
        UserService(get())
    }
}
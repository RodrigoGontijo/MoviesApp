package com.hornet.movies.core.di

import com.hornet.movies.data.repository.MovieRepository
import com.hornet.movies.data.repository.impl.MovieRepositoryImpl
import com.hornet.movies.data.service.MoviesService
import org.koin.dsl.module

val networkModule = module {
    single { MoviesService.getInstance() } // TMDb service
    single<MovieRepository> { MovieRepositoryImpl(get()) } // Repository implementation
}
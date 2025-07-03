package com.hornet.movies.core.di

import com.hornet.movies.domain.GetGenresUseCase
import com.hornet.movies.domain.GetMovieDetailsUseCase
import com.hornet.movies.domain.GetTopRatedMoviesUseCase
import com.hornet.movies.features.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { GetTopRatedMoviesUseCase(get()) }
    factory { GetMovieDetailsUseCase(get()) }
    factory { GetGenresUseCase(get()) }

    viewModel { HomeViewModel(get(), get(), get()) }
}
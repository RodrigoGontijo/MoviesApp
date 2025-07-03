package com.hornet.movies.domain

import com.hornet.movies.data.model.movie.MovieDetails
import com.hornet.movies.data.repository.MovieRepository

class GetMovieDetailsUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): MovieDetails {
        return repository.getMovieDetails(movieId)
    }
}
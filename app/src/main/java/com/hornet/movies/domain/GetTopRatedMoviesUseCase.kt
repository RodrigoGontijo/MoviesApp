package com.hornet.movies.domain

import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.data.repository.MovieRepository

class GetTopRatedMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int): List<Movie> {
        return repository.getTopRatedMovies(page)
    }
}
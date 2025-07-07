package com.hornet.movies.domain

import com.hornet.movies.data.repository.MovieRepository

class GetGenresUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): Map<Int, String> {
        return repository.getGenres()
    }
}

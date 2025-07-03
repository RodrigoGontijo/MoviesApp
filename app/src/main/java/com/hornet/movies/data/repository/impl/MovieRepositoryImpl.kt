package com.hornet.movies.data.repository.impl

import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.data.model.movie.MovieDetails
import com.hornet.movies.data.repository.MovieRepository
import com.hornet.movies.data.service.MoviesService

class MovieRepositoryImpl(
    private val service: MoviesService
) : MovieRepository {

    override suspend fun getTopRatedMovies(page: Int): List<Movie> {
        val results = service.getTopMovies(page)
        return results.results.filter { it.vote_average >= 7.0 }
    }

    override suspend fun getMovieDetails(id: Int): MovieDetails {
        return service.getMovieDetails(id)
    }

    override suspend fun getGenres(): Map<Int, String> {
        return service.getGenres().genres.associateBy({ it.id }, { it.name })
    }
}
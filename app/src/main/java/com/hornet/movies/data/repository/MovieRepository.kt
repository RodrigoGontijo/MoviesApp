package com.hornet.movies.data.repository

import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.data.model.movie.MovieDetails

interface MovieRepository {
    suspend fun getTopRatedMovies(page: Int): List<Movie>
    suspend fun getMovieDetails(id: Int): MovieDetails
    suspend fun getGenres(): Map<Int, String>
}
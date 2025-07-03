package com.hornet.movies.features.home

import com.hornet.movies.data.model.movie.Movie

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val expandedMovieIds: Set<Int> = emptySet(),
    val selectedGenreId: Int? = null,
    val genreCount: Map<Int, Int> = emptyMap() // genreId -> count
)
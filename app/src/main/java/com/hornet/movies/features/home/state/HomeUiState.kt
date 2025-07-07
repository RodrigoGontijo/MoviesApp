package com.hornet.movies.features.home.state

import com.hornet.movies.data.model.movie.Movie

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val expandedMovieIds: Set<Int> = emptySet(),
    val selectedGenreId: Int? = null,
    val genreCount: Map<Int, String> = emptyMap(), // genreId -> count
    val genres: Map<Int, String> = emptyMap(),
    val errorMessage: String? = null,
    val loadingMovieIds: Set<Int> = emptySet() // Movies ids that have loading things happening
)
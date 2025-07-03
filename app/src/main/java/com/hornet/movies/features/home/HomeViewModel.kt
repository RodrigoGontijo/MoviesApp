package com.hornet.movies.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.domain.usecase.GetGenresUseCase
import com.hornet.movies.domain.usecase.GetMovieDetailsUseCase
import com.hornet.movies.domain.usecase.GetTopRatedMoviesUseCase
import com.hornet.movies.features.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTopRatedMovies: GetTopRatedMoviesUseCase,
    private val getMovieDetails: GetMovieDetailsUseCase,
    private val getGenres: GetGenresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val movieList = mutableListOf<Movie>()
    private var currentPage = 1
    private var endReached = false

    init {
        loadMoreMovies()
    }

    fun loadMoreMovies() {
        if (endReached || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val newMovies = getTopRatedMovies(currentPage)

                if (newMovies.isEmpty()) {
                    endReached = true
                } else {
                    currentPage++
                    movieList.addAll(newMovies)

                    val genreCount = movieList
                        .flatMap { it.genre_ids }
                        .groupingBy { it }
                        .eachCount()

                    _uiState.value = _uiState.value.copy(
                        movies = movieList.toList(),
                        isLoading = false,
                        genreCount = genreCount
                    )
                }
            } catch (e: Exception) {
                // Ignored, as error handling is not required
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleExpanded(id: Int) {
        val expanded = _uiState.value.expandedMovieIds.toMutableSet()

        if (expanded.contains(id)) {
            expanded.remove(id)
        } else {
            expanded.add(id)
            fetchMovieDetails(id)
        }

        _uiState.value = _uiState.value.copy(expandedMovieIds = expanded)
    }

    private fun fetchMovieDetails(id: Int) {
        viewModelScope.launch {
            try {
                val details = getMovieDetails(id)

                val updatedMovie = movieList.find { it.id == id }?.copy(
                    director = details.director?.name,
                    actors = details.actors.map { it.name },
                    productionCompany = details.productionCompany?.name
                )

                updatedMovie?.let { updated ->
                    val index = movieList.indexOfFirst { it.id == id }
                    movieList[index] = updated

                    _uiState.value = _uiState.value.copy(
                        movies = movieList.toList()
                    )
                }
            } catch (e: Exception) {
                // Details loading failed — silently ignore
            }
        }
    }

    fun selectGenre(genreId: Int?) {
        _uiState.value = _uiState.value.copy(selectedGenreId = genreId)
    }
}
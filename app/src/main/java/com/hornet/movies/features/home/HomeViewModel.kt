package com.hornet.movies.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.domain.GetGenresUseCase
import com.hornet.movies.domain.GetMovieDetailsUseCase
import com.hornet.movies.domain.GetTopRatedMoviesUseCase
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
        getAllGenres()
    }

    private fun getAllGenres() {
        viewModelScope.launch {
            try {
                val genres = getGenres()
                _uiState.value = _uiState.value.copy(genres = genres)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error when loading genres: ${e.localizedMessage}"
                )
            }
        }
    }

    fun loadMoreMovies() {
        if (endReached || _uiState.value.isLoading) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val newMovies = getTopRatedMovies(currentPage)
                val genresMap = getGenres()

                movieList.addAll(newMovies)
                currentPage++
                endReached = newMovies.isEmpty()

                val genreCount: Map<Int, String> = movieList
                    .flatMap { it.genre_ids }
                    .groupingBy { it }
                    .eachCount()
                    .mapValues { (genreId, count) ->
                        "${genresMap[genreId] ?: "Unknown"} ($count)"
                    }

                _uiState.value = _uiState.value.copy(
                    movies = movieList,
                    isLoading = false,
                    genres = genresMap,
                    genreCount = genreCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error when loading movies ${e.localizedMessage}"
                )
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

                val movieIndex = movieList.indexOfFirst { it.id == id }
                if (movieIndex == -1) return@launch

                val original = movieList[movieIndex]
                val updated = original.copy(
                    director = details.director?.name ?: "",
                    actors = details.actors.map { it.name },
                    productionCompany = details.productionCompany?.name ?: ""
                )

                movieList[movieIndex] = updated

                val filteredMovies = _uiState.value.selectedGenreId?.let { genreId ->
                    movieList.filter { it.genre_ids.contains(genreId) }
                } ?: movieList

                _uiState.value = _uiState.value.copy(
                    movies = filteredMovies
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error when loading movie details: ${e.localizedMessage}"
                )
            }
        }
    }

    fun selectGenre(genreId: Int?) {
        val currentSelected = _uiState.value.selectedGenreId
        val newSelected = if (currentSelected == genreId) null else genreId

        val filteredMovies = if (newSelected == null) {
            movieList
        } else {
            movieList.filter { it.genre_ids.contains(newSelected) }
        }

        _uiState.value = _uiState.value.copy(
            selectedGenreId = newSelected,
            movies = filteredMovies
        )
    }
}
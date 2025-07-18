package com.hornet.movies.features.home.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.domain.GetGenresUseCase
import com.hornet.movies.domain.GetMovieDetailsUseCase
import com.hornet.movies.domain.GetTopRatedMoviesUseCase
import com.hornet.movies.features.home.state.HomeUiState
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
                movieList.addAll(newMovies)
                currentPage++
                endReached = newMovies.isEmpty()

                _uiState.value = _uiState.value.copy(
                    movies = newMovies,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error when loading movies: ${e.localizedMessage}"
                )
            }
        }

        viewModelScope.launch {
            try {
                val genresMap = getGenres()

                val genreCount: Map<Int, String> = movieList
                    .flatMap { it.genre_ids }
                    .groupingBy { it }
                    .eachCount()
                    .mapValues { (genreId, count) ->
                        "${genresMap[genreId] ?: "Unknown"} ($count)"
                    }

                val selectedGenreId = _uiState.value.selectedGenreId
                val filteredMovies = if (selectedGenreId != null) {
                    movieList.filter { it.genre_ids.contains(selectedGenreId) }
                } else {
                    movieList
                }

                _uiState.value = _uiState.value.copy(
                    movies = filteredMovies,
                    isLoading = false,
                    genres = genresMap,
                    genreCount = genreCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error when loading movies: ${e.localizedMessage}"
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

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loadingMovieIds = _uiState.value.loadingMovieIds + movieId
            )

            try {
                val movieDetails = getMovieDetails(movieId)

                val index = movieList.indexOfFirst { it.id == movieId }
                if (index != -1) {
                    val updatedMovie = movieList[index].copy(
                        director = movieDetails.director?.name.orEmpty(),
                        actors = movieDetails.actors.mapNotNull { it.name },
                        productionCompany = movieDetails.productionCompany?.name.orEmpty()
                    )

                    movieList[index] = updatedMovie

                    // Reaply filter based on the selected genre
                    val selectedGenreId = _uiState.value.selectedGenreId
                    val filteredMovies = if (selectedGenreId != null) {
                        movieList.filter { it.genre_ids.contains(selectedGenreId) }
                    } else {
                        movieList
                    }

                    // Forces recomposition with a new instance of the list
                    _uiState.value = _uiState.value.copy(
                        movies = filteredMovies.toList()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.cause?.message)
            } finally {
                _uiState.value = _uiState.value.copy(
                    loadingMovieIds = _uiState.value.loadingMovieIds - movieId
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
            movies = filteredMovies.toList() // Forces recomposition
        )
    }
}
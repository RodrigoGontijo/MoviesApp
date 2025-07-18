package com.hornet.movies.features.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hornet.movies.features.home.viewmodel.HomeViewModel
import com.hornet.movies.features.home.view.components.GenreBar
import com.hornet.movies.features.home.view.components.MovieItem
import com.hornet.movies.features.home.view.components.PosterDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPoster by rememberSaveable { mutableStateOf<String?>(null) }

    // Show snackbar when an error message appears in the UI state
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Movie list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                state = listState
            ) {
                items(uiState.movies) { movie ->
                    val isExpanded = uiState.expandedMovieIds.contains(movie.id)
                    val isHighlighted = uiState.selectedGenreId != null &&
                            movie.genre_ids.contains(uiState.selectedGenreId)
                    val isLoadingDetails = uiState.loadingMovieIds.contains(movie.id)

                    MovieItem(
                        movie = movie,
                        isExpanded = isExpanded,
                        isHighlighted = isHighlighted,
                        isLoadingDetails = isLoadingDetails,
                        onItemClick = { viewModel.toggleExpanded(movie.id) },
                        onPosterClick = {
                            selectedPoster = "https://image.tmdb.org/t/p/w500${movie.poster}"
                        }
                    )
                }

                // Show loading indicator at the end of the list
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Genre filter bar
            GenreBar(
                genreCounts = uiState.genreCount,
                selectedGenreId = uiState.selectedGenreId,
                onGenreClick = { viewModel.selectGenre(it) }
            )
        }
    }

    // Full screen poster dialog
    PosterDialog(
        posterUrl = selectedPoster,
        onDismiss = { selectedPoster = null }
    )

    // Scroll to top when genre filter changes
    LaunchedEffect(uiState.selectedGenreId) {
        listState.animateScrollToItem(0)
    }

    // Load more movies when the user scrolls near the bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisible = visibleItems.lastOrNull()?.index ?: return@collect
                val total = listState.layoutInfo.totalItemsCount
                if (lastVisible >= total - 3) {
                    viewModel.loadMoreMovies()
                }
            }
    }
}

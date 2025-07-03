package com.hornet.movies.features.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hornet.movies.features.home.HomeUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var selectedPoster by remember { mutableStateOf<String?>(null) }

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

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = listState
        ) {
            items(uiState.movies) { movie ->
                val isExpanded = uiState.expandedMovieIds.contains(movie.id)
                val isHighlighted = uiState.selectedGenreId?.let {
                    movie.genre_ids.contains(it)
                } ?: false

                MovieItem(
                    movie = movie,
                    isExpanded = isExpanded,
                    isHighlighted = isHighlighted,
                    onItemClick = { viewModel.toggleExpanded(movie.id) },
                    onPosterClick = {
                        selectedPoster = "https://image.tmdb.org/t/p/w500${movie.poster}"
                    }
                )
            }

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

        GenreBar(
            genreCounts = uiState.genreCount,
            selectedGenreId = uiState.selectedGenreId,
            onGenreClick = { viewModel.selectGenre(it) }
        )
    }

    PosterDialog(
        posterUrl = selectedPoster,
        onDismiss = { selectedPoster = null }
    )
}
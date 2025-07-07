package com.hornet.movies.features.home.view.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.hornet.movies.data.model.movie.Movie

@Composable
fun MovieItem(
    movie: Movie,
    isExpanded: Boolean,
    isHighlighted: Boolean,
    isLoadingDetails: Boolean,
    onItemClick: () -> Unit,
    onPosterClick: () -> Unit
) {
    val backgroundColor = if (isHighlighted)
        MaterialTheme.colorScheme.tertiaryContainer
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick() },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w185${movie.poster}"),
                    contentDescription = "Poster",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPosterClick() },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title ?: "No Title",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.overview ?: "No Overview",
                        maxLines = 3,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rating: ${movie.vote_average}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoadingDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Director: ${movie.director ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Actors: ${movie.actors?.joinToString(", ") ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Company: ${movie.productionCompany ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            movie.backdrop.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500$it"),
                    contentDescription = "Backdrop",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
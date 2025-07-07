package com.hornet.movies.features.home.view.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenreBar(
    genreCounts: Map<Int, String>,
    selectedGenreId: Int?,
    onGenreClick: (Int?) -> Unit
) {
    if (genreCounts.isEmpty()) return

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { onGenreClick(null) },
            label = { Text("All") },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (selectedGenreId == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                labelColor = if (selectedGenreId == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        genreCounts
            .filter { it.key > 0 }
            .forEach { (genreId, count) ->
                AssistChip(
                    onClick = { onGenreClick(genreId) },
                    label = { Text(count) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedGenreId == genreId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (selectedGenreId == genreId) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
            }
    }
}
package com.hornet.movies.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GenreBar(
    genreCounts: Map<Int, Int>,
    selectedGenreId: Int?,
    onGenreClick: (Int?) -> Unit
) {
    if (genreCounts.isEmpty()) return

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "Todos" para remover filtro
        GenreChip(
            label = "All",
            isSelected = selectedGenreId == null,
            onClick = { onGenreClick(null) }
        )

        genreCounts
            .filter { it.value > 0 }
            .forEach { (genreId, count) ->
                val label = "$genreId ($count)" // pode substituir pelo nome do gênero se desejar
                GenreChip(
                    label = label,
                    isSelected = selectedGenreId == genreId,
                    onClick = { onGenreClick(genreId) }
                )
            }
    }
}

@Composable
fun GenreChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
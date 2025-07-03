package com.hornet.movies.features.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter

@Composable
fun PosterDialog(
    posterUrl: String?,
    onDismiss: () -> Unit
) {
    if (posterUrl == null) return

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = posterUrl),
                contentDescription = "Full Poster",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(2f / 3f) // proporção aproximada de pôster
                    .clipToBounds()
            )
        }
    }
}
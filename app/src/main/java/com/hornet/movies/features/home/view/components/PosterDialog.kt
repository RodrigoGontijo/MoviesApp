package com.hornet.movies.features.home.view.components

import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.hornet.movies.core.utils.TextVisionFinder

@Composable
fun PosterDialog(
    posterUrl: String?,
    onDismiss: () -> Unit
) {
    if (posterUrl == null) return

    val textFinder = remember { TextVisionFinder() }
    val detectedBoxes = remember { mutableStateListOf<RectF>() }

    // Executa OCR quando uma nova imagem for aberta
    LaunchedEffect(posterUrl) {
        detectedBoxes.clear()
        textFinder.findText(posterUrl) { boxes ->
            detectedBoxes.addAll(boxes)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 8.dp
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = posterUrl),
                    contentDescription = "Full Poster",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(2f / 3f)
                        .padding(8.dp)
                        .drawWithContent {
                            drawContent()

                            val imageSize = size
                            detectedBoxes.forEach { rect ->
                                drawRect(
                                    color = Color.Black, // ← Caixa preta sólida
                                    topLeft = Offset(
                                        rect.left * imageSize.width,
                                        rect.top * imageSize.height
                                    ),
                                    size = Size(
                                        rect.width() * imageSize.width,
                                        rect.height() * imageSize.height
                                    )
                                )
                            }
                        }
                )
            }
        }
    }
}
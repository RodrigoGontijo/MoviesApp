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
    /****************** Code Addition
     ******************/
    val textFinder = remember{TextVisionFinder()}
    textFinder.findText(posterUrl)
    /****************** Code Addition
     ******************/

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
                        /****************** Code Addition
                         ******************/
                        .drawWithContent {
                            //Draw Original Content
                            drawContent()
                            //Additional Draws
                            val imageBounds = this.size
                            // Demo rectangle at the center of the image and taking up 20% of width and height
                            val demoRect = RectF(0.4f,0.4f, 0.6f,0.6f)
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(demoRect.left * imageBounds.width,demoRect.top * imageBounds.height),
                                size = Size(demoRect.width() * imageBounds.width, demoRect.height() * imageBounds.height)
                            )
                        },
                        /****************** Code Addition
                         ******************/
                )
            }
        }
    }
}
package com.hornet.movies.core.utils

import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextVisionFinder {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Detect text in an image and return normalized bounding boxes (values between 0 and 1)
     */
    fun findText(url: String, onResult: (List<RectF>) -> Unit) {
        loadBitmapFromUrl(url) { bitmap ->
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val normalizedBoxes = visionText.textBlocks.mapNotNull { block ->
                        block.boundingBox?.toRectF()?.normalize(image.width, image.height)
                    }

                    Log.e("TextVisionFinder", "====== Text Found ========================")
                    visionText.textBlocks.forEach {
                        Log.e("TextVisionFinder", "Text: ${it.text} — Box: ${it.boundingBox}")
                    }
                    Log.e("TextVisionFinder", "========================================")

                    onResult(normalizedBoxes)
                }
                .addOnFailureListener {
                    onResult(emptyList())
                }
        }
    }
}
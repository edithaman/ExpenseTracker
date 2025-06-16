package com.example.expensetracker.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

object OCRProcessor {
    fun processImage(
        context: Context,
        imageUri: Uri,
        onResult: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onResult(visionText.text)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: IOException) {
            onError(e)
        }
    }
}
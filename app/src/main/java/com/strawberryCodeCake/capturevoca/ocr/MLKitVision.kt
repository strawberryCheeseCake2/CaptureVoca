package com.strawberryCodeCake.capturevoca.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MLKitVision {

    companion object {


        suspend fun recognizeText(img: Bitmap): Text = suspendCoroutine { continuation ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(img, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

        suspend fun recognizeWords(img: Bitmap):
                MutableList<Text.Element> = suspendCoroutine { continuation ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(img, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val wordList = extractWords(visionText)
                    continuation.resume(wordList)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

        fun extractWords(text: Text): MutableList<Text.Element> {
            val wordList = mutableListOf<Text.Element>()

            text.textBlocks.forEach { paragraph ->
                val lines = paragraph.lines
                lines.forEach { line ->
                    val words = line.elements
                    words.forEach { word ->
                        wordList.add(word)
                    }
                }
            }

            return wordList
        }

    }
}

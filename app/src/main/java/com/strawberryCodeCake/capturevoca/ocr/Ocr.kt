package com.strawberryCodeCake.capturevoca.ocr

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.google.mlkit.vision.text.Text
import org.opencv.core.*

class Ocr {
    init {

    }

    companion object {

        data class HighlightRecognitionResult(
            val words: MutableList<Text.Element>,
            val highlightTable: MutableList<Boolean>
        )

        @Throws(Exception::class)
        private fun findHighlightedWords(
            maskMat: Mat, words: MutableList<Text.Element>,
            thresholdPercentage: Int
        ): MutableList<Boolean> {
            val wordCount = words.size
            val isHighlightedTable = MutableList<Boolean>(wordCount) { false }

            words.forEachIndexed { index, word ->

                // Get bounding box position and size of word
                val box = word.boundingBox ?: throw Exception("no boundingBox for word")
                val x = box.left
                val y = box.top
                val h = box.height()
                val w = box.width()

                // Calculate threshold number of pixels for the area of the bounding box
                val rectThreshold = (w * h * thresholdPercentage) / 100

                // Select region of interest from image mask
                val regionOfInterest = maskMat.submat(y, y + h, x, x + w)

                // Count white pixels in ROI using Core.countNonZero
                val whitePixelCount = Core.countNonZero(regionOfInterest)

                // Set word as highlighted if its white pixels exceed the threshold value
                if (whitePixelCount > rectThreshold) {
                    isHighlightedTable[index] = true
                }
            }

            return isHighlightedTable
        }

        @Throws(Exception::class)
        suspend fun getHighlightedTexts(
            context: Context,
            bitmap: Bitmap
        ): HighlightRecognitionResult {
            // Load Image
            val orgMat = OpenCV.bitmapToMat(bitmap)

            // Grayscale and apply Otsu's threshold
            val imgThresh = OpenCV.grayscaleImage(orgMat)

            // Color Segmentation
            val imgMask = OpenCV.maskImage(orgMat)

            // Noise reduction
            val denoisedImgMask = OpenCV.denoiseImage(imgMask)

            // Apply mask on original image
            val maskedOrgImg = OpenCV.applyMask(orgMat, denoisedImgMask) // param check!

            // Apply mask on thresholded image
            val maskedThreshImg = OpenCV.applyMask(imgThresh, denoisedImgMask)

            // Get Words
            val result = MLKitVision.recognizeText(bitmap)
            val words = MLKitVision.extractWords(result)
            var isHighlightedTable = mutableListOf<Boolean>()


            try {
                isHighlightedTable = findHighlightedWords(denoisedImgMask, words, 25)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                println(e.message)
            }

            return HighlightRecognitionResult(
                words = words,
                highlightTable = isHighlightedTable
            )
        }


        fun getHighlightedWords(
            words: MutableList<Text.Element>,
            highlightTable: MutableList<Boolean>
        ): MutableList<Text.Element> {

            val highlightedWords = mutableListOf<Text.Element>()

            highlightTable.forEachIndexed { index, isHighlighted ->
                if (isHighlighted) {
                    val highlighted = words[index]
                    highlightedWords.add(words[index])
                }
            }

            return highlightedWords
        }

        fun extractSentence(
            keyword: Text.Element,
            words: MutableList<Text.Element>,
            highlightTable: MutableList<Boolean>
        ): String? {

            val size = words.size

            var forwardSearchResult = -1
            var backwardSearchResult = -1

            var startPos = -1

            words.forEachIndexed { index, _word ->
                val _text = _word.text
                if (_text == keyword.text && highlightTable[index]) {
                    startPos = index
                }
            }
            if (startPos == -1) return null

            for (i in startPos..<size) {

                if (words[i].text.contains(".")) {
                    forwardSearchResult = i
                    break
                }
            }

            for (i in (startPos - 1) downTo 0) {
                if (words[i].text.contains(".")) {
                    backwardSearchResult = i
                    break
                }
            }

            var isFirstSentence = false

            if (forwardSearchResult == -1) return null
            if (backwardSearchResult == -1) isFirstSentence = true

            val subStart = if (!isFirstSentence) {
                backwardSearchResult + 1
            } else {
                0
            }

            val subList = words.subList(subStart, forwardSearchResult + 1)
            val sentence = subList.map { it.text }.joinToString(" ")



            return sentence
        }


    } // companion object END


}
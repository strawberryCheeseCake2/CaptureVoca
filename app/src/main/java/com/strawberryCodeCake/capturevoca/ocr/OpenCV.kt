package com.strawberryCodeCake.capturevoca.ocr

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


class OpenCV {


    init {

    }
    companion object {


        fun matToBitmap(mat: Mat): Bitmap {
            val bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, bitmap)
            return bitmap
        }

        fun bitmapToMat(bitmap: Bitmap): Mat {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            return mat
        }


        fun maskImage(srcMat: Mat): Mat {

            val resMat = Mat()

            // HSV for Yellow range
//            val lowerHsv = Scalar(22.0, 30.0, 30.0)
            val lowerHsv = Scalar(000.0,0.0,100.0)
            val upperHsv = Scalar(221.0, 105.0, 255.0)
//            val upperHsv = Scalar(0.0, 45.0, 255.0)

            // Image to HSV
            Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_RGB2HSV)
//            Imgproc.cvtColor(resMat, resMat, Imgproc.COLOR_XYZ2BGR)
//            val newRes = Mat()
            // Color segmentation with lower and upper threshold ranges to obtain a binary image
            Core.inRange(resMat, lowerHsv, upperHsv, resMat)
            Core.bitwise_not(resMat, resMat)

            return resMat
        }

        fun denoiseImage(srcMat: Mat): Mat {
            val resMat = Mat()
            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))

//            Imgproc.morphologyEx(srcMat, resMat, Imgproc.MORPH_OPEN, kernel, Point(), 2) // iteration = 1?
            Imgproc.morphologyEx(srcMat, resMat, Imgproc.MORPH_OPEN, kernel) // iteration = 1?

            return resMat
        }

        fun applyMask(srcMat: Mat, maskMat: Mat): Mat {
            val resMat = Mat()
            Core.bitwise_and(srcMat, srcMat, resMat, maskMat)
            return resMat
        }

        fun grayscaleImage(srcMat: Mat): Mat {
            val resMat = Mat()

            // Convert to grayscale
            Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_RGB2GRAY)

            // Threshold image
            Imgproc.threshold(resMat, resMat, 0.0, 255.0, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY)

            return resMat
        }

        fun grayscale_bitmap(bitmap: Bitmap): Bitmap {
            // Create OpenCV mat object and copy content from bitmap
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            // Convert to grayscale
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

            // Threshold image
            Imgproc.threshold(mat, mat, 0.0, 255.0, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY)

            // Make a mutable bitmap to copy grayscale image
            val mutableBitmap = bitmap.copy(bitmap.config, true)
            Utils.matToBitmap(mat, mutableBitmap)

            return mutableBitmap
        }
    }


}
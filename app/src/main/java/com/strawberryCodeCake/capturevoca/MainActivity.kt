package com.strawberryCodeCake.capturevoca

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.strawberryCodeCake.capturevoca.ui.theme.CaptureVocaTheme
import org.opencv.android.OpenCVLoader


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initLocal()

        setContent {
            CaptureVocaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
                CaptureVocaApp(context = applicationContext)
            }
        }

    }


}

@Composable
fun TestImage(src: Bitmap, modifier: Modifier = Modifier) {
    Image(bitmap = src.asImageBitmap(), contentDescription = "bitmap image")
}

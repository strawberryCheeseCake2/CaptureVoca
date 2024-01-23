package com.strawberryCodeCake.capturevoca.ui.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.net.Uri
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.*
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.strawberryCodeCake.capturevoca.ui.AppViewModelProvider
import com.strawberryCodeCake.capturevoca.ui.navigation.NavigationDestination
import com.strawberryCodeCake.capturevoca.R
import kotlinx.coroutines.*
import java.io.FileOutputStream


private const val PAGE_HEIGHT = 792F
private const val PAGE_WIDTH = 1120F
private const val DEFAULT_SPACING = 100F
private const val SMALL_SPACING = 35F
private const val PAGE_MARGIN = 100F

object PdfGeneratorDestination : NavigationDestination {
    override val route: String = "pdf_generator"
    override val titleRes: Int = R.string.pdf_generator_title
}

@Composable
fun PdfGenerator(
    navigateUp: () -> Unit, viewModel: PdfGeneratorViewModel = viewModel(
        factory = AppViewModelProvider.Factory
    )
) {

    val uiState by viewModel.pdfGeneratorUiState.collectAsState()

    val context = LocalContext.current


    suspend fun generatePdf(uri: Uri) {
        withContext(Dispatchers.IO) {
            val pdfDocument: PdfDocument = PdfDocument()

            val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "w")
            val fileOutputStream = FileOutputStream(fileDescriptor!!.fileDescriptor)

            drawPDF(vocaList = uiState.vocaList, pdfDocument)

            pdfDocument.writeTo(fileOutputStream)

            pdfDocument.close()
            fileOutputStream.close()
        }
    }

    fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }


    val launcher = rememberLauncherForActivityResult(
        CreateDocument("application/pdf")
    ) { uri ->

        val safeUri = uri ?: run {
            navigateUp()
            return@rememberLauncherForActivityResult
        }

        CoroutineScope(Dispatchers.IO).launch {

            try {
                generatePdf(safeUri)
                showToast(message = context.getString(R.string.pdf_success_toast))
            } catch (e: Exception) {
                Log.e("e", e.message ?: "")
                showToast(message = context.getString(R.string.pdf_error_toast))
            }



            CoroutineScope(Dispatchers.Main).launch {
                navigateUp()
            }
        }

    }



    LaunchedEffect(uiState) {

        if (uiState.vocaList.isNotEmpty()) {
            launcher.launch("${context.getString(R.string.pdf_default_file_name)}.pdf")
        }

    }


}

private fun drawPDF(vocaList: List<Voca>, pdfDocument: PdfDocument) {

    val wordPaint = TextPaint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        textSize = 20F
        color = Color.BLACK
    }

    val meaningPaint = TextPaint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 16F
        color = Color.BLACK
    }

    val examplePaint = TextPaint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        textSize = 15F
        color = Color.BLACK
    }

    var startY = PAGE_MARGIN

    fun createPage(): Page {
        val info = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH.toInt(),
            PAGE_HEIGHT.toInt(),
            1
        ).create()
        return pdfDocument.startPage(info)
    }

    val initialPage = createPage()
    var currentPage = initialPage

    fun moveOnToNextPage() {
        startY = PAGE_MARGIN
        pdfDocument.finishPage(currentPage)

        val info = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH.toInt(),
            PAGE_HEIGHT.toInt(),
            1
        ).create()
        currentPage = pdfDocument.startPage(info)
    }

    fun drawLayout(canvas: Canvas, layout: StaticLayout, startX: Float, startY: Float) {
        canvas.save()
        canvas.translate(startX, startY)
        layout.draw(canvas)
        canvas.restore()
    }


    vocaList.forEachIndexed { index, voca ->

        val wordWidth = 80
        val wordLayout = StaticLayout.Builder
            .obtain(voca.word, 0, voca.word.length, wordPaint, wordWidth)
            .build()
        val meaningLayout = StaticLayout.Builder
            .obtain(
                voca.meaning,
                0,
                voca.meaning.length,
                meaningPaint,
                (PAGE_WIDTH * 0.65).toInt()
            )
            .build()
        val exampleLayout = StaticLayout.Builder
            .obtain(
                voca.example,
                0,
                voca.example.length,
                examplePaint,
                (PAGE_WIDTH * 0.7).toInt()
            )
            .build()


        val endY = startY + meaningLayout.height + exampleLayout.height
        if (endY > PAGE_HEIGHT) {
            moveOnToNextPage()
        }


        drawLayout(currentPage.canvas, wordLayout, startX = 100F, startY = startY)
        drawLayout(
            currentPage.canvas,
            meaningLayout,
            startX = PAGE_MARGIN + wordWidth + SMALL_SPACING,
            startY = startY
        )
        drawLayout(
            currentPage.canvas,
            exampleLayout,
            startX = PAGE_MARGIN + wordWidth + SMALL_SPACING,
            startY = startY + meaningLayout.height + SMALL_SPACING
        )

        startY += (meaningLayout.height + SMALL_SPACING + exampleLayout.height)
            .toFloat() + DEFAULT_SPACING

        if (index == vocaList.size - 1) {
            pdfDocument.finishPage(currentPage)
        }

    }

}
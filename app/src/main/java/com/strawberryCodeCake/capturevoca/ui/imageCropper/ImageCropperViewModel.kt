package com.strawberryCodeCake.capturevoca.ui.imageCropper

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strawberryCodeCake.capturevoca.data.dictionary.DictionaryRepository
import com.strawberryCodeCake.capturevoca.data.dictionary.model.toVoca
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.strawberryCodeCake.capturevoca.data.room.voca.VocaRepository
import com.strawberryCodeCake.capturevoca.data.translation.TranslationRepository
import com.strawberryCodeCake.capturevoca.ocr.Ocr
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ImageCropperViewModel(
    private val vocaRepository: VocaRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val translationRepository: TranslationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ImageCropperUiState>(ImageCropperUiState())

    val uiState: StateFlow<ImageCropperUiState> = _uiState.asStateFlow()

    fun setIsLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isLoading = isLoading)
        }
    }

    fun addHighlightedVoca(context: Context, image: Bitmap, completionHandler: () -> Unit) {
        viewModelScope.launch {
            setIsLoading(true)

            val highlightedResult = Ocr.getHighlightedTexts(context, image)
            val words = highlightedResult.words
            val highlightTable = highlightedResult.highlightTable

            Toast.makeText(context, words.map { it.text }.joinToString(","), Toast.LENGTH_LONG).show()

            val highlightedWordIndices = mutableListOf<Int>()
            highlightTable.forEachIndexed { index, b ->
                if (b) highlightedWordIndices.add(index)
            }

            val highlightedWords = Ocr.getHighlightedWords(words, highlightTable)


            highlightedWords.forEach { word ->
                println(word)
                val sentence =
                    Ocr.extractSentence(
                        keyword = word,
                        words = words,
                        highlightTable = highlightTable
                    ) ?: ""

                val entry = dictionaryRepository.getDictionaryEntry(word.text)

                val vocaData = entry?.toVoca()

                val translatedMeaning = translationRepository.getTranslation(vocaData?.meaning ?: "_")


                vocaRepository.insertVoca(
                    Voca(
                        word = vocaData?.word ?: word.text,
                        meaning = translatedMeaning,
                        example = sentence
                    )
                )


            }

            setIsLoading(false)
            completionHandler()
        }


    }

}

data class ImageCropperUiState(val isLoading: Boolean = false)
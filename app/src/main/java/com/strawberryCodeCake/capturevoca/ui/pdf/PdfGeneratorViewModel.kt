package com.strawberryCodeCake.capturevoca.ui.pdf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strawberryCodeCake.capturevoca.data.room.voca.VocaRepository
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PdfGeneratorViewModel(vocaRepository: VocaRepository): ViewModel() {
    val pdfGeneratorUiState: StateFlow<PdfGeneratorUiState> =
        vocaRepository.getAllVocaStream().map { PdfGeneratorUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PdfGeneratorUiState()
            )

    init {
        viewModelScope.launch {
            val s = vocaRepository.getAllVocaStream().collect {
                println(it.size)
            }
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class PdfGeneratorUiState(val vocaList: List<Voca> = listOf())
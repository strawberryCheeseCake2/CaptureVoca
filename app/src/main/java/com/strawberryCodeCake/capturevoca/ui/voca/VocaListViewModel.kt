package com.strawberryCodeCake.capturevoca.ui.voca

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.strawberryCodeCake.capturevoca.data.room.voca.VocaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */

class VocaListViewModel(val vocaRepository: VocaRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */

    val vocaListUiState: StateFlow<VocaListUiState> =
        vocaRepository.getAllVocaStream().map { VocaListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = VocaListUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun deleteVoca(id: Int) {
        viewModelScope.launch {
            vocaRepository.deleteVocaById(id)
        }

    }


}

/**
 * Ui State for HomeScreen
 */
data class VocaListUiState(val vocaList: List<Voca> = listOf())


package com.strawberryCodeCake.capturevoca.ui.voca

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strawberryCodeCake.capturevoca.data.dataStore.UserPreferencesRepository
import com.strawberryCodeCake.capturevoca.data.dictionary.DictionaryRepository
import com.strawberryCodeCake.capturevoca.data.dictionary.model.toVoca
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.strawberryCodeCake.capturevoca.data.room.voca.VocaRepository
import com.strawberryCodeCake.capturevoca.data.translation.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VocaEntryViewModel(
    val savedStateHandle: SavedStateHandle,
    private val vocaRepository: VocaRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val translationRepository: TranslationRepository
) : ViewModel() {

    private var vocaId: Int = checkNotNull(savedStateHandle[VocaEntryDestination.vocaIdArg])

    private val isEditMode: Boolean = vocaId > 0

    private val _uiState = MutableStateFlow(VocaEntryUiState())

    val uiState: StateFlow<VocaEntryUiState> = _uiState.asStateFlow()

    private var didLoadVocaDetails by mutableStateOf<Boolean>(false)


    init {
        loadVocaToEdit()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(
        vocaDetails: VocaDetails = _uiState.value.vocaDetails,
        shouldTranslateMeaning: Boolean = _uiState.value.shouldTranslateMeaning
    ) {
        val isValid = vocaDetails.word.trim() != "" && vocaDetails.meaning.trim() != ""

        _uiState.update { currentState ->
            return@update currentState.copy(
                isEntryValid = isValid,
                vocaDetails = vocaDetails,
            )
        }

    }

    private fun loadVocaToEdit() {
        if (!isEditMode) return

        viewModelScope.launch {

            userPreferencesRepository.getUserPreferences().collect { preferences ->
                val shouldTranslateMeaning = preferences.translateMeaning
                println("hrere!!")
                println(shouldTranslateMeaning)
                vocaRepository.getVocaStream(vocaId)
                    .filterNotNull()
                    .collect { fetchedVoca ->
                        updateUiState(fetchedVoca.toVocaDetails(), shouldTranslateMeaning)
                        didLoadVocaDetails = true
                    }
            } // userPreferencesRepository.getUserPreferences().collect END


        } // viewModelScope END

    } // loadVocaToEdit END

    fun saveVoca(completionHandler: () -> Unit = {}) {
        val _uiStateValue = _uiState.value

        if (!_uiStateValue.isEntryValid) return

        viewModelScope.launch {

            val _vocaDetail = _uiStateValue.vocaDetails

            if (isEditMode) {

                val vocaToUpdate = Voca(
                    id = _vocaDetail.id,
                    word = _vocaDetail.word,
                    meaning = _vocaDetail.meaning,
                    example = _vocaDetail.example
                )

                vocaRepository.updateVoca(vocaToUpdate)
            } else {
                val vocaToInsert = Voca(
                    word = _vocaDetail.word,
                    meaning = _vocaDetail.meaning,
                    example = _vocaDetail.example
                )

                vocaRepository.insertVoca(vocaToInsert)
            }

            completionHandler()
        } // viewModelScope END


    }

    fun loadDictionaryEntry(onCheckCompleted: () -> Unit = {}) {
        val _details = _uiState.value.vocaDetails
        val _word = _details.word
        val _meaning = _details.meaning
        val _example = _details.example

        if (_word.trim() == "" ||
            (!didLoadVocaDetails && vocaId > 0) ||
            (_meaning.trim() != "" && _example.trim() != "")
        ) return
        onCheckCompleted()

        viewModelScope.launch {

            val entry = dictionaryRepository.getDictionaryEntry(_word)

            val vocaData = entry?.toVoca() ?: return@launch

            val _newMeaning = vocaData.meaning
            val _newExample = vocaData.example


            var translatedMeaning = _newMeaning

            try {
                translatedMeaning = translationRepository.getTranslation(_newMeaning)
            } catch (e: Exception) {
                Log.e("Translation Error", e.message ?: "")
            }


            val meaningToUpdate = if (_meaning.trim() == "") {
                translatedMeaning
            } else {
                _meaning
            }

            val exampleToUpdate = if (_example.trim() == "") {
                _newExample
            } else {
                _example
            }


            updateUiState(
                vocaDetails = VocaDetails(
                    id = vocaId,
                    word = entry.word ?: _word,
                    meaning = meaningToUpdate,
                    example = exampleToUpdate
                )
            )

        }
    }


}


/**
 * Represents Ui State for an Item.
 */
data class VocaEntryUiState(
    val vocaDetails: VocaDetails = VocaDetails(),
    val isEntryValid: Boolean = false,
    val shouldTranslateMeaning: Boolean = true
)

data class VocaDetails(
    val id: Int = 0,
    val word: String = "",
    val meaning: String = "",
    val example: String = "",
)

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Voca.toVocaUiState(isEntryValid: Boolean = false): VocaEntryUiState = VocaEntryUiState(
    vocaDetails = this.toVocaDetails(),
    isEntryValid = isEntryValid,
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Voca.toVocaDetails(): VocaDetails = VocaDetails(
    id = id,
    word = word,
    meaning = meaning,
    example = example,
)


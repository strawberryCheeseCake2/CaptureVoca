package com.strawberryCodeCake.capturevoca.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strawberryCodeCake.capturevoca.data.dataStore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SettingsViewModel(private val userPreferencesRepository: UserPreferencesRepository) :
    ViewModel() {


    private var _uiState =
        MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    // get Flow Data
    private fun loadSettings() {

        viewModelScope.launch {
            userPreferencesRepository.getUserPreferences().collect { userPref ->
                updateUiState(translateMeaningSetting = userPref.translateMeaning)
            }
        }

    }

    fun updateTranslateMeaningSetting(translateMeaningSetting: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateTranslateMeaning(translateMeaning = translateMeaningSetting)
        }
    }

    fun updateUiState(translateMeaningSetting: Boolean) {
        _uiState.update { currentState ->
            return@update currentState.copy(translateMeaning = translateMeaningSetting)
        }
    }



}

data class SettingsUiState(val translateMeaning: Boolean = true)
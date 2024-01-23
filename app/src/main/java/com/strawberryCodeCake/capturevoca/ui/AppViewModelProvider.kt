package com.strawberryCodeCake.capturevoca.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.*
import com.strawberryCodeCake.capturevoca.CaptureVocaApplication
import com.strawberryCodeCake.capturevoca.ui.imageCropper.ImageCropperViewModel
import com.strawberryCodeCake.capturevoca.ui.pdf.PdfGeneratorViewModel
import com.strawberryCodeCake.capturevoca.ui.setting.SettingsViewModel
import com.strawberryCodeCake.capturevoca.ui.voca.*


object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for VocaEntryViewModel
        initializer {
            VocaEntryViewModel(
                this.createSavedStateHandle(),
                captureVocaApplication().container.vocaRepository,
                captureVocaApplication().container.dictionaryRepository,
                captureVocaApplication().container.userPreferencesRepository,
                captureVocaApplication().container.translationRepository
            )
        }

        // Initializer for VocaListViewModel
        initializer {
            VocaListViewModel(captureVocaApplication().container.vocaRepository)
        }

        // Initializer for ImageCropperViewModel
        initializer {
            ImageCropperViewModel(
                captureVocaApplication().container.vocaRepository,
                captureVocaApplication().container.dictionaryRepository,
                captureVocaApplication().container.translationRepository
            )
        }

        // Initializer for PdfGeneratorViewModel
        initializer {
            PdfGeneratorViewModel(
                captureVocaApplication().container.vocaRepository
            )
        }

        // Initializer for SettingsViewModel
        initializer {
            SettingsViewModel(
                captureVocaApplication().container.userPreferencesRepository
            )
        }

    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.captureVocaApplication(): CaptureVocaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CaptureVocaApplication)
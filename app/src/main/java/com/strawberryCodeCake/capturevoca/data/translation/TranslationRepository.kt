package com.strawberryCodeCake.capturevoca.data.translation

import android.util.Log
import com.strawberryCodeCake.capturevoca.data.dataStore.UserPreferencesRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TranslationRepository(
    val userPreferencesRepository: UserPreferencesRepository
) {

    private fun getTranslator(): Translator {
        // Create an English-German translator:
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.KOREAN)
            .build()

        return Translation.getClient(options)
    }

    private suspend fun downloadModelIfNeeded(): Boolean = suspendCoroutine { continuation ->
        val translator = getTranslator()

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                continuation.resume(true)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
                continuation.resume(false)
            }
    }


    suspend fun translate(text: String): String = suspendCoroutine { continuation ->

        val translator = getTranslator()

        CoroutineScope(Dispatchers.IO).launch {

            async { downloadModelIfNeeded() }.await()

            translator.translate(text).addOnSuccessListener { translatedText ->
                // Translation successful.
                val pattern = "\\s+(\\d+)\\."
                val replacement = "\n$1."

                val formattedResult = Regex(pattern).replace(translatedText, replacement).trim()
                continuation.resume(formattedResult)
                translator.close()
            }.addOnFailureListener { exception ->
                // Error.
                Log.e("Translation Error", exception.message ?: "")
                translator.close()
                continuation.resumeWithException(exception)
            }
        }

    }

    suspend fun getTranslation(text: String): String {

        val preferences = userPreferencesRepository.getUserPreferences().first() // suspend
        if (!preferences.translateMeaning) return text

        val translatedText = translate(text) //  suspend

        return translatedText
    }


}
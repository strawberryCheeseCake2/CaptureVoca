package com.strawberryCodeCake.capturevoca.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.strawberryCodeCake.capturevoca.data.dataStore.UserPreferencesRepository
import com.strawberryCodeCake.capturevoca.data.dictionary.DictionaryRepository
import com.strawberryCodeCake.capturevoca.data.room.voca.*
import com.strawberryCodeCake.capturevoca.data.translation.TranslationRepository

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val vocaRepository: VocaRepository
    val dictionaryRepository: DictionaryRepository
    val userPreferencesRepository: UserPreferencesRepository
    val translationRepository: TranslationRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val vocaRepository: VocaRepository by lazy {
        OfflineVocaRepository(VocaDatabase.getDatabase(context).vocaDao())
    }

    override val dictionaryRepository: DictionaryRepository by lazy {
        DictionaryRepository()
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore = context.dataStore)
    }

    override val translationRepository: TranslationRepository by lazy {
        TranslationRepository(userPreferencesRepository)
    }

}
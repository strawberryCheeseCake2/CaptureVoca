package com.strawberryCodeCake.capturevoca.data.dataStore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

enum class SortOrder {
    NONE,
    BY_DEADLINE,
    BY_PRIORITY,
    BY_DEADLINE_AND_PRIORITY
}

//data class UserPreferences(
//    val showCompleted: Boolean,
//    val sortOrder: SortOrder
//)

private const val USER_PREFERENCES_NAME = "user_preferences"

/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private val TAG: String = "UserPreferencesRepo"

    private object PreferencesKeys {
        val TRANSLATE_MEANING = booleanPreferencesKey("translate_meaning")
    }

    /**
     * Get the user preferences flow.
     */
    private val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    fun getUserPreferences(): Flow<UserPreferences> {
        return userPreferencesFlow
    }



    /**
     * Edit preferences
     */
    suspend fun updateTranslateMeaning(translateMeaning: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TRANSLATE_MEANING] = translateMeaning
        }
    }

    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())



    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val translateMeaning = preferences[PreferencesKeys.TRANSLATE_MEANING] ?: true
        return UserPreferences(translateMeaning = translateMeaning)
    }
}
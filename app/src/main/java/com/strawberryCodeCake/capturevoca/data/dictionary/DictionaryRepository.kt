package com.strawberryCodeCake.capturevoca.data.dictionary

import android.util.Log
import com.strawberryCodeCake.capturevoca.data.dictionary.model.DictionaryEntry

class DictionaryRepository {
    suspend fun getDictionaryEntry(word: String): DictionaryEntry? {
        val strippedWord = word.replace(Regex("[\\p{Punct}&&[^_]]"), "")

        var entryList: List<DictionaryEntry?> = emptyList()

        try {
            entryList = DictionaryApi.retrofitService.getDictionaryEntryList(strippedWord)
        } catch (e: Exception) {
            Log.e("Exception", e.message ?: "")
        }

        if (entryList.isEmpty()) return null

        return entryList.first()
    }


}
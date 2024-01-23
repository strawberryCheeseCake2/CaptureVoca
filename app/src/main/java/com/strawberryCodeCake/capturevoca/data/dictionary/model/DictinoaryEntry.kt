package com.strawberryCodeCake.capturevoca.data.dictionary.model

import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.google.gson.annotations.SerializedName

data class DictionaryEntry(

    @SerializedName("word") var word: String? = null,
    @SerializedName("phonetic") var phonetic: String? = null,
    @SerializedName("phonetics") var phonetics: ArrayList<Phonetics> = arrayListOf(),
    @SerializedName("origin") var origin: String? = null,
    @SerializedName("meanings") var meanings: ArrayList<Meanings> = arrayListOf()

)

data class Phonetics(
    @SerializedName("text") var text: String? = null,
    @SerializedName("audio") var audio: String? = null

)

data class Definitions(

    @SerializedName("definition") var definition: String? = null,
    @SerializedName("example") var example: String? = null,
    @SerializedName("synonyms") var synonyms: ArrayList<String> = arrayListOf(),
    @SerializedName("antonyms") var antonyms: ArrayList<String> = arrayListOf()

)

data class Meanings(

    @SerializedName("partOfSpeech") var partOfSpeech: String? = null,
    @SerializedName("definitions") var definitions: ArrayList<Definitions> = arrayListOf()

)

fun DictionaryEntry.toVoca(): Voca? {
    val entry = this

    val _word = entry.word ?: return null
    val _meanings = entry.meanings
    if (_meanings.isEmpty()) return null

    val definitions = _meanings.first().definitions

    if (definitions.isEmpty()) return null

    var _newMeaning = ""
    definitions.forEachIndexed { index, definiton ->
        val _def = definiton.definition
        val _synonyms = definiton.synonyms.joinToString(", ")

        if (_def != null) {
            _newMeaning += "${index + 1}. ${_def}"

            if (_synonyms.isNotEmpty()) {
                _newMeaning += "\n  = $_synonyms"
            }

            if (index != definitions.size - 1) _newMeaning += "\n"
        }

    }

    val _newExample = definitions.first().example ?: ""

    return Voca(id = -1, word = _word, meaning = _newMeaning, example = _newExample)
}

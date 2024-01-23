package com.strawberryCodeCake.capturevoca.data.room.voca.model

import androidx.room.*


@Entity
data class Voca(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // starts from 1
    val word: String,
    val meaning: String,
    val example: String
)

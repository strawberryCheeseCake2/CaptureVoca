package com.strawberryCodeCake.capturevoca.ui.voca.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.strawberryCodeCake.capturevoca.ui.voca.VocaDetails
import com.strawberryCodeCake.capturevoca.R

@Composable
fun VocaInputForm(
    vocaDetails: VocaDetails,
    modifier: Modifier = Modifier,
    onValueChange: (VocaDetails) -> Unit = {},
    onWordFocusLost: () -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        DefaultTextField(
            value = vocaDetails.word,
            onValueChange = { newText ->
                onValueChange(vocaDetails.copy(word = newText))
            },
            labelString = stringResource(R.string.word_input_form),
            onFocusLost = onWordFocusLost
        )

        DefaultTextField(
            value = vocaDetails.meaning,
            onValueChange = { newText ->
                onValueChange(vocaDetails.copy(meaning = newText))
            },
            labelString = stringResource(R.string.meaning_input_form)
        )

        DefaultTextField(
            value = vocaDetails.example,
            onValueChange = { newText ->
                onValueChange(vocaDetails.copy(example = newText))
            },
            labelString = stringResource(R.string.example_input_form)
        )



    }
}
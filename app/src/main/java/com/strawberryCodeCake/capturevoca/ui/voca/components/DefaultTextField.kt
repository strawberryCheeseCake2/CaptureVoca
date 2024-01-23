package com.strawberryCodeCake.capturevoca.ui.voca.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit = {},
    enabled: Boolean = true,
    labelString: String
) {
    TextField(
        value = value,
        onValueChange = { newText ->
            onValueChange(newText)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            focusedContainerColor = Color.Transparent,

            ),
        label = { Text(labelString) },
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .onFocusChanged { state ->
                if (!state.isFocused) {
                    onFocusLost()
                }
            },
        shape = RoundedCornerShape(100.dp),
        enabled = enabled,
    )
}
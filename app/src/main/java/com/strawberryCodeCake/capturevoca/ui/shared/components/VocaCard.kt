package com.strawberryCodeCake.capturevoca.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca

@Composable
fun VocaCard(voca: Voca, onItemClick: (Int) -> Unit, modifier: Modifier = Modifier) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
        ),
        modifier = modifier
            .height(140.dp)
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                onItemClick(voca.id)
            }
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(15.dp)
            ),
        shape = RoundedCornerShape(16.dp)


    ) {
        Text(
            text = voca.word,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp),
        )

        Text(
            text = voca.meaning,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}


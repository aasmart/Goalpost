package io.aasmart.goalpost.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun FieldHeader(
    title: String,
    subTitle: String
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subTitle,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Light
        )
    }
}
package io.aasmart.goalpost.src.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

private val weekdays = setOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun GoalTimePeriodDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Text(text = "Create Time Period", style = MaterialTheme.typography.headlineMedium)

            var expanded by remember { mutableStateOf(false) }
            val selectedIndices = remember { mutableStateListOf<Int>() }

            MultiDropdown(
                label = "",
                expanded = expanded,
                menuHeight = 120.dp,
                onExpandedChange = { expanded = it },
                itemNames = weekdays,
                selectedIndices = selectedIndices.toSet(),
                onItemClicked = { selectedIndices.add(it) }
            )
        }
    }
}
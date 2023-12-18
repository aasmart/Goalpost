package io.aasmart.goalpost.src.compose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    currentTime: Instant,
    datePickerState: DatePickerState,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    dateValidator: (Long) -> Boolean
) {
    val selectedTime = datePickerState.selectedDateMillis?.let { Instant.ofEpochMilli(it) } ?: currentTime
    val zonedSelectedTime = LocalDateTime.ofInstant(selectedTime, ZoneOffset.UTC)

    if(expanded) {
        DatePickerDialog(
            onDismissRequest = { onExpandedChange(false) },
            confirmButton = {
                Button(
                    onClick = {
                        onExpandedChange(false)
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onExpandedChange(false)
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = dateValidator
            )
        }
    }

    val dateString =
        "${zonedSelectedTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} " +
        "${zonedSelectedTime.dayOfMonth}, ${zonedSelectedTime.year}"

    TextField(
        value = dateString,
        onValueChange = {},
        readOnly = true,
        label = { Text("Goal End Date") },
        leadingIcon = {
            Icon(Icons.Default.DateRange, "Select date")
        },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it !is PressInteraction.Release)
                            return@collect
                        onExpandedChange(true)
                    }
                }
            },
        modifier = Modifier
            .fillMaxWidth()
    )
}
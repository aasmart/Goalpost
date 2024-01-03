package io.aasmart.goalpost.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.aasmart.goalpost.R
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.scheduleReflectionAlarm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    label: @Composable () -> Unit,
    timePickerState: TimePickerState,
    onExpandedChange: (Boolean) -> Unit,
    confirm: @Composable () -> Unit,
    dismiss: @Composable () -> Unit
) {
    Dialog(onDismissRequest = { onExpandedChange(false) }) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            label()
            TimePicker(
                state = timePickerState,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                dismiss()
                confirm()
            }
        }
    }
}
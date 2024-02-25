package io.aasmart.goalpost.compose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.aasmart.goalpost.R

private fun hourMinutesToString(
    minutes: Int,
    hours: Int,
): String {
    val selectedMinuteStr = if (minutes < 10) "0${minutes}" else minutes

    return if (hours > 12)
        "${hours - 12}:${selectedMinuteStr} PM"
    else if (hours == 0)
        "12:${selectedMinuteStr} AM"
    else if (hours == 12)
        "12:${selectedMinuteStr} PM"
    else
        "${hours}:${selectedMinuteStr} AM"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePicker(
    timePickerState: TimePickerState,
    onTimePickerSubmit: (hours: Int, minutes: Int) -> Unit,
    timePickerDialogVisible: Boolean,
    onVisibilityChanged: (Boolean) -> Unit,
    timePickerDialogLabel: @Composable () -> Unit,
    anchor: @Composable () -> Unit
) {
    anchor()

    if(timePickerDialogVisible) {
        TimePickerDialog(
            label = timePickerDialogLabel,
            timePickerState = timePickerState,
            onExpandedChange = { onVisibilityChanged(it) },
            dismiss = {
                TextButton(onClick = { onVisibilityChanged(false) }) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
            },
            confirm = {
                TextButton(
                    onClick = {
                        onTimePickerSubmit(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldTimePicker(
    timePickerState: TimePickerState,
    selectedHours: Int,
    selectedMinutes: Int,
    onTimePickerSubmit: (hours: Int, minutes: Int) -> Unit,
    timePickerDialogVisible: Boolean,
    onVisibilityChanged: (Boolean) -> Unit,
    label: @Composable () -> Unit,
    timePickerDialogLabel: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = {},
    supportingText: @Composable () -> Unit = {},
    enabled: Boolean = true
) {
    TimePicker(
        timePickerState = timePickerState,
        onTimePickerSubmit = onTimePickerSubmit,
        onVisibilityChanged = onVisibilityChanged,
        timePickerDialogVisible = timePickerDialogVisible,
        timePickerDialogLabel = timePickerDialogLabel
    ) {
        TextField(
            value = hourMinutesToString(
                hours = selectedHours,
                minutes = selectedMinutes
            ),
            onValueChange = {},
            label = label,
            readOnly = true,
            leadingIcon = leadingIcon,
            supportingText = supportingText,
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it !is PressInteraction.Release)
                                return@collect
                            onVisibilityChanged(true)
                        }
                    }
                },
            modifier = modifier
        )
    }
}
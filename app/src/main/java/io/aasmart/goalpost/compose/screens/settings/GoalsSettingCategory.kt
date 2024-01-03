package io.aasmart.goalpost.compose.screens.settings

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.components.TimePickerDialog
import io.aasmart.goalpost.data.settingsDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCategoryContent() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var timePickerDialogExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val settingsState by context.settingsDataStore.data.collectAsStateWithLifecycle(initialValue = null)
    if(settingsState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(48.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
        return
    }

    /*Convert reflection time in MS to hours and minutes in order
    * to be displayed */
    val reflectionHours = settingsState
        ?.goalReflectionTimeMs
        ?.div(1000 * 60 * 60)
        ?.toInt()
    val reflectionMinutes = settingsState
        ?.goalReflectionTimeMs
        ?.minus(reflectionHours?.times(1000 * 60 * 60) ?: 0)
        ?.div(1000 * 60)
        ?.toInt()

    var selectedHour by remember {
        mutableIntStateOf(reflectionHours ?: 0)
    }
    var selectedMinute by remember {
        mutableIntStateOf(reflectionMinutes ?: 0)
    }

    // Create the label displayed in the text field
    val selectedMinuteStr = if(selectedMinute < 10) "0${selectedMinute}" else selectedMinute
    val selectedTimeStr =
        if(selectedHour > 12)
            "${selectedHour - 12}:${selectedMinuteStr} PM"
        else if(selectedHour == 0)
            "12:${selectedMinuteStr} AM"
        else if(selectedHour == 12)
            "12:${selectedMinuteStr} PM"
        else
            "${selectedHour}:${selectedMinuteStr} AM"

    TextField(
        value = selectedTimeStr,
        onValueChange = {},
        label = {
            Text(text = stringResource(id = R.string.reflection_time))
        },
        readOnly = true,
        leadingIcon = {
            Icon(Icons.Filled.DateRange, contentDescription = null)
        },
        supportingText = {
             Text(
                 text = stringResource(id = R.string.reflection_time_description),
                 fontWeight = FontWeight.Light
             )
        },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it !is PressInteraction.Release)
                            return@collect
                        timePickerDialogExpanded = true
                    }
                }
            },
        modifier = Modifier.fillMaxWidth()
    )

    // Create the time picker
    val timePickerState = rememberTimePickerState(
        selectedHour,
        selectedMinute,
        false
    )

    val submitReflectionTimePicker = {
        selectedHour = timePickerState.hour
        selectedMinute = timePickerState.minute

        val selectedMillis =
            (selectedHour * 3600 + selectedMinute * 60) * 1000L

        coroutineScope.launch {
            context.settingsDataStore.updateData {
                return@updateData it.toBuilder()
                    .setGoalReflectionTimeMs(selectedMillis)
                    .build()
            }
        }

        timePickerDialogExpanded = false
    }

    if(timePickerDialogExpanded) {
        TimePickerDialog(
            label = {
                Text(
                    text = stringResource(id = R.string.reflection_time_picker),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(8.dp)
                )
            },
            timePickerState = timePickerState,
            onExpandedChange = { timePickerDialogExpanded = it },
            dismiss = {
                TextButton(onClick = { timePickerDialogExpanded = false }) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
            },
            confirm = {
                TextButton(
                    onClick = submitReflectionTimePicker
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        )
    }
}
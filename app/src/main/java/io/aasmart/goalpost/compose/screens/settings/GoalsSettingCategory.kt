package io.aasmart.goalpost.compose.screens.settings

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
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.components.TextFieldTimePicker
import io.aasmart.goalpost.data.settingsDataStore
import kotlinx.coroutines.launch

/** Converts a time in milliseconds to hours and minutes
 *
 * @param millis The time in milliseconds
 * @return A pair where the first value is the hours, and the second is minutes
 */
private fun millisToHoursAndMinutes(millis: Long): Pair<Int, Int> {
    val reflectionHours = millis
        .div(1000 * 60 * 60)
        .toInt()
    val reflectionMinutes = millis
        .minus(reflectionHours.times(1000 * 60 * 60))
        .div(1000 * 60)
        .toInt()

    return reflectionHours to reflectionMinutes
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCategoryContent() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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

    var reflectionTimePickerDialogVisible by remember {
        mutableStateOf(false)
    }
    val selectedReflectionTime = settingsState?.goalReflectionTimeMs?.let {
        millisToHoursAndMinutes(it)
    } ?: (0 to 0)

    var reflectionSelectedHours by remember {
        mutableIntStateOf(selectedReflectionTime.first)
    }
    var reflectionSelectedMinutes by remember {
        mutableIntStateOf(selectedReflectionTime.second)
    }

    // Create the label displayed in the text field

    val reflectionTimePickerState = rememberTimePickerState(
        reflectionSelectedHours,
        reflectionSelectedMinutes,
        false
    )

    TextFieldTimePicker(
        timePickerState = reflectionTimePickerState,
        selectedHours = reflectionSelectedHours,
        selectedMinutes = reflectionSelectedMinutes,
        onTimePickerSubmit = { hour, minute ->
            reflectionSelectedHours = hour
            reflectionSelectedMinutes = minute

            val selectedMillis =
                (reflectionSelectedHours * 3600 + reflectionSelectedMinutes * 60) * 1000L

            coroutineScope.launch {
                context.settingsDataStore.updateData {
                    return@updateData it.toBuilder()
                        .setGoalReflectionTimeMs(selectedMillis)
                        .build()
                }
            }

            reflectionTimePickerDialogVisible = false
        },
        timePickerDialogVisible = reflectionTimePickerDialogVisible,
        onVisibilityChanged = { reflectionTimePickerDialogVisible = it },
        label = {
            Text(text = stringResource(id = R.string.reflection_time))
        },
        leadingIcon = {
            Icon(Icons.Filled.DateRange, contentDescription = null)
        },
        supportingText = {
            Text(
                text = stringResource(id = R.string.reflection_time_description),
                fontWeight = FontWeight.Light
            )
        },
        timePickerDialogLabel = {
            Text(
                text = stringResource(id = R.string.reflection_time_picker),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(8.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    var morningReminderTimePickerDialogVisible by remember {
        mutableStateOf(false)
    }
    val selectedMorningReminderTime = settingsState?.morningReminderTimeMs?.let {
        millisToHoursAndMinutes(it)
    } ?: (0 to 0)

    var selectedMorningReminderHours by remember {
        mutableIntStateOf(selectedMorningReminderTime.first)
    }
    var selectedMorningReminderMinutes by remember {
        mutableIntStateOf(selectedMorningReminderTime.second)
    }

    // Create the label displayed in the text field

    val morningReminderTimePickerState = rememberTimePickerState(
        selectedMorningReminderHours,
        selectedMorningReminderMinutes,
        false
    )

    TextFieldTimePicker(
        timePickerState = morningReminderTimePickerState,
        selectedHours = selectedMorningReminderHours,
        selectedMinutes = selectedMorningReminderMinutes,
        onTimePickerSubmit = { hour, minute ->
            selectedMorningReminderHours = hour
            selectedMorningReminderMinutes = minute

            val selectedMillis =
                (selectedMorningReminderHours * 3600 + selectedMorningReminderMinutes * 60) * 1000L

            coroutineScope.launch {
                context.settingsDataStore.updateData {
                    return@updateData it.toBuilder()
                        .setMorningReminderTimeMs(selectedMillis)
                        .build()
                }
            }

            morningReminderTimePickerDialogVisible = false
        },
        timePickerDialogVisible = morningReminderTimePickerDialogVisible,
        onVisibilityChanged = { morningReminderTimePickerDialogVisible = it },
        label = {
            Text(text = stringResource(id = R.string.morning_reminder_time))
        },
        leadingIcon = {
            Icon(Icons.Filled.DateRange, contentDescription = null)
        },
        supportingText = {
            Text(
                text = stringResource(id = R.string.morning_reminder_description),
                fontWeight = FontWeight.Light
            )
        },
        timePickerDialogLabel = {
            Text(
                text = stringResource(id = R.string.choose_morning_reminder_time),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(8.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    // The field for the mid-day reminder time

    var middayReminderTimePickerDialogVisible by remember {
        mutableStateOf(false)
    }
    val selectedMiddayReminderTime = settingsState?.midDayReminderTimeMs?.let {
        millisToHoursAndMinutes(it)
    } ?: (0 to 0)

    var selectedMiddayReminderHours by remember {
        mutableIntStateOf(selectedMiddayReminderTime.first)
    }
    var selectedMiddayReminderMinutes by remember {
        mutableIntStateOf(selectedMiddayReminderTime.second)
    }

    val middayReminderTimePickerState = rememberTimePickerState(
        selectedMiddayReminderHours,
        selectedMiddayReminderMinutes,
        false
    )

    TextFieldTimePicker(
        timePickerState = middayReminderTimePickerState,
        selectedHours = selectedMiddayReminderHours,
        selectedMinutes = selectedMiddayReminderMinutes,
        onTimePickerSubmit = { hour, minute ->
            selectedMiddayReminderHours = hour
            selectedMiddayReminderMinutes = minute

            val selectedMillis =
                (selectedMiddayReminderHours * 3600 + selectedMiddayReminderMinutes * 60) * 1000L

            coroutineScope.launch {
                context.settingsDataStore.updateData {
                    return@updateData it.toBuilder()
                        .setMidDayReminderTimeMs(selectedMillis)
                        .build()
                }
            }

            middayReminderTimePickerDialogVisible = false
        },
        timePickerDialogVisible = middayReminderTimePickerDialogVisible,
        onVisibilityChanged = { middayReminderTimePickerDialogVisible = it },
        label = {
            Text(text = stringResource(id = R.string.midday_reminder_time))
        },
        leadingIcon = {
            Icon(Icons.Filled.DateRange, contentDescription = null)
        },
        supportingText = {
            Text(
                text = stringResource(id = R.string.midday_reminder_description),
                fontWeight = FontWeight.Light
            )
        },
        timePickerDialogLabel = {
            Text(
                text = stringResource(id = R.string.choose_midday_reminder_time),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(8.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    // The field for the mid-day reminder time

    var eveningReminderTimePickerDialogVisible by remember {
        mutableStateOf(false)
    }
    val selectedEveningReminderTime = settingsState?.eveningReminderTimeMs?.let {
        millisToHoursAndMinutes(it)
    } ?: (0 to 0)

    var selectedEveningReminderHours by remember {
        mutableIntStateOf(selectedEveningReminderTime.first)
    }
    var selectedEveningReminderMinutes by remember {
        mutableIntStateOf(selectedEveningReminderTime.second)
    }

    val eveningReminderTimePickerState = rememberTimePickerState(
        selectedEveningReminderHours,
        selectedEveningReminderMinutes,
        false
    )

    TextFieldTimePicker(
        timePickerState = eveningReminderTimePickerState,
        selectedHours = selectedEveningReminderHours,
        selectedMinutes = selectedEveningReminderMinutes,
        onTimePickerSubmit = { hour, minute ->
            selectedEveningReminderHours = hour
            selectedEveningReminderMinutes = minute

            val selectedMillis =
                (selectedEveningReminderHours * 3600 + selectedEveningReminderMinutes * 60) * 1000L

            coroutineScope.launch {
                context.settingsDataStore.updateData {
                    return@updateData it.toBuilder()
                        .setEveningReminderTimeMs(selectedMillis)
                        .build()
                }
            }

            eveningReminderTimePickerDialogVisible = false
        },
        timePickerDialogVisible = eveningReminderTimePickerDialogVisible,
        onVisibilityChanged = { eveningReminderTimePickerDialogVisible = it },
        label = {
            Text(text = stringResource(id = R.string.evening_reminder_time))
        },
        leadingIcon = {
            Icon(Icons.Filled.DateRange, contentDescription = null)
        },
        supportingText = {
            Text(
                text = stringResource(id = R.string.evening_reminder_description),
                fontWeight = FontWeight.Light
            )
        },
        timePickerDialogLabel = {
            Text(
                text = stringResource(id = R.string.choose_evening_reminder_time),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(8.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}
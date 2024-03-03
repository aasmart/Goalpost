package io.aasmart.goalpost.compose.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import io.aasmart.goalpost.Settings
import io.aasmart.goalpost.compose.components.TextFieldTimePicker
import io.aasmart.goalpost.data.settingsDataStore
import kotlinx.coroutines.CoroutineScope
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
private fun GoalReminderItem(
    index: Int,
    settings: Settings?,
    coroutineScope: CoroutineScope
) {
    if(settings == null)
        return

    val context = LocalContext.current

    var reminderTimePickerDialogVisible by remember {
        mutableStateOf(false)
    }
    val selectedReminderTime =
        if(index < settings.reminderNotifTimesCount) {
            settings.reminderNotifTimesList
                ?.get(index)
                ?.let { millisToHoursAndMinutes(it) }
                ?: (0 to 0)
        } else {
            0 to 0
        }

    var selectedReminderHours by remember {
        mutableIntStateOf(selectedReminderTime.first)
    }
    var selectedReminderMinutes by remember {
        mutableIntStateOf(selectedReminderTime.second)
    }

    // Create the label displayed in the text field

    val reminderTimePickerState = rememberTimePickerState(
        selectedReminderHours,
        selectedReminderMinutes,
        false
    )

    var checked by rememberSaveable {
        mutableStateOf(
            if(index >= settings.reminderEnabledCount)
                false
            else
                settings.getReminderEnabled(index)
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Switch(
            onCheckedChange = {
                coroutineScope.launch {
                    checked = !checked
                    context.settingsDataStore.updateData {
                        return@updateData if (index >= settings.reminderEnabledCount) {
                            it.toBuilder()
                                .addReminderEnabled(checked)
                                .build()
                        } else {
                            it.toBuilder()
                                .setReminderEnabled(index, checked)
                                .build()
                        }
                    }
                }
            },
            checked = checked
        )
        TextFieldTimePicker(
            timePickerState = reminderTimePickerState,
            selectedHours = selectedReminderHours,
            selectedMinutes = selectedReminderMinutes,
            onTimePickerSubmit = { hour, minute ->
                selectedReminderHours = hour
                selectedReminderMinutes = minute

                val selectedMillis =
                    (selectedReminderHours * 3600 + selectedReminderMinutes * 60) * 1000L

                coroutineScope.launch {
                    context.settingsDataStore.updateData {
                        return@updateData if (index >= settings.reminderNotifTimesCount) {
                            it.toBuilder()
                                .addReminderNotifTimes(selectedMillis)
                                .build()
                        } else {
                            it.toBuilder()
                                .setReminderNotifTimes(index, selectedMillis)
                                .build()
                        }
                    }
                }

                reminderTimePickerDialogVisible = false
            },
            timePickerDialogVisible = reminderTimePickerDialogVisible,
            onVisibilityChanged = { reminderTimePickerDialogVisible = it },
            label = {
                Text(text = stringResource(id = R.string.goal_reminder_time))
            },
            leadingIcon = {
                Icon(Icons.Filled.DateRange, contentDescription = null)
            },
            supportingText = {
                Text(
                    text = stringResource(id = R.string.goal_reminder_description),
                    fontWeight = FontWeight.Light
                )
            },
            timePickerDialogLabel = {
                Text(
                    text = stringResource(id = R.string.choose_reminder_time),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(8.dp)
                )
            },
            enabled = checked,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCategoryContent() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val settingsState by context.settingsDataStore.data
        .collectAsStateWithLifecycle(initialValue = null)

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

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.schedule_goal_reminders),
                style = MaterialTheme.typography.titleSmall
            )
        }
        item {
            GoalReminderItem(index = 0, settings = settingsState, coroutineScope = coroutineScope)
        }
        item {
            GoalReminderItem(index = 1, settings = settingsState, coroutineScope = coroutineScope)
        }
        item {
            GoalReminderItem(index = 2, settings = settingsState, coroutineScope = coroutineScope)
        }
    }
}
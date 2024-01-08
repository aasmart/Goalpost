package io.aasmart.goalpost.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold
import io.aasmart.goalpost.compose.components.TextFieldDatePicker
import io.aasmart.goalpost.compose.components.TextFieldDropdown
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalInterval
import io.aasmart.goalpost.utils.InputUtils
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
private fun CreateGoalButton(
    goalName: String,
    goalDescription: String,
    goalCompletionDate: Long,
    goalInterval: GoalInterval,
    addGoal : suspend (goal: Goal) -> Unit,
    goalManagerHandle: () -> Unit,
    inputsValid: Boolean
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            val goal = Goal.createGoalWithReflections(
                title = goalName,
                description = goalDescription,
                timePeriod = goalInterval,
                beginDate = System.currentTimeMillis(),
                completionDate = goalCompletionDate
            )

            scope.launch {
                addGoal(goal)
            }
            goalManagerHandle()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        enabled = inputsValid
    ) {
        Text(text = stringResource(id = R.string.create_goal))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    goalpostNav: GoalpostNav,
    addGoal: suspend (goal: Goal) -> Unit,
) {
    var goalName by rememberSaveable { mutableStateOf("") }
    var goalDescription by rememberSaveable { mutableStateOf("") }

    val isNameValid = InputUtils.isValidLength(
        goalName.trim(),
        Goal.NAME_MIN_LENGTH,
        Goal.NAME_MAX_LENGTH
    )
    val isDescriptionValid = InputUtils.isValidLength(
        goalDescription.trim(),
        Goal.DESCRIPTION_MIN_LENGTH
    )

    GoalpostNavScaffold(nav = goalpostNav) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Create Your New Goal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )

            Text(text="Define Your Goal")

            // Name Input
            OutlinedTextField(
                value = goalName,
                onValueChange = { goalName = it },
                label = {
                    Text(text = stringResource(id = R.string.goal_name))
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                isError = !isNameValid,
                supportingText = {
                    if(!isNameValid)
                        Text(
                            text = stringResource(id = R.string.between_num_characters)
                                .replace(
                                    "{LABEL}",
                                    stringResource(id = R.string.goal_name)
                                )
                                .replace(
                                    "{MIN}",
                                    Goal.NAME_MIN_LENGTH.toString()
                                )
                                .replace(
                                    "{MAX}",
                                    Goal.NAME_MAX_LENGTH.toString()
                                )
                        )
                    else
                        Text(
                            text = stringResource(id = R.string.num_characters)
                                .replace(
                                    "{NUM_CHARACTERS}",
                                    "${goalName.trim().length}/${Goal.NAME_MAX_LENGTH}"
                                )
                        )
                }
            )

            // Description Input
            OutlinedTextField(
                value = goalDescription,
                onValueChange = { goalDescription = it },
                label = {
                    Text(text = stringResource(id = R.string.goal_description))
                },
                isError = !isDescriptionValid,
                supportingText = {
                    if(!isDescriptionValid)
                        Text(
                            text = stringResource(id = R.string.at_least_num_characters)
                                .replace(
                                    "{LABEL}",
                                    stringResource(id = R.string.goal_description)
                                )
                                .replace(
                                    "{MIN}",
                                    Goal.DESCRIPTION_MIN_LENGTH.toString()
                                )
                        )
                    else
                        Text(
                            text = stringResource(id = R.string.num_characters)
                                .replace(
                                    "{NUM_CHARACTERS}",
                                    goalDescription.trim().length.toString()
                                )
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Text(text = "Meeting your Goal")

            // End time date picker

            val now = Instant.now()
            var datePickerExpanded by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState()

            TextFieldDatePicker(
                label = stringResource(id = R.string.goal_completion_date),
                currentTime = now,
                datePickerState = datePickerState,
                expanded = datePickerExpanded,
                onExpandedChange = { datePickerExpanded = it },
                dateValidator = { time ->
                    /* This drove me insane. Since time picker is in UTC, the min time
                    * is inaccurate in the current time zone. If we treat the current instant
                    * as local time, we offset it to UTC time and it works*/

                    val offsetMilli = OffsetDateTime
                        .ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
                        .offset.totalSeconds * 1000L
                    val currentMilli = Instant.now()
                        .plusMillis(offsetMilli)
                        .truncatedTo(ChronoUnit.DAYS)
                        .toEpochMilli()

                    time >= currentMilli
                }
            )

            var remindIntervalExpanded by remember {
                mutableStateOf(false)
            }
            var remindSelectedIndex by rememberSaveable {
                mutableIntStateOf(0)
            }

            TextFieldDropdown(
                label = "Remind Interval",
                expanded = remindIntervalExpanded,
                menuHeight = 150.dp,
                selectedIndex = remindSelectedIndex,
                items = listOf("Daily", "Weekly", "Bi-Weekly", "Monthly", "Custom"),
                onItemClicked = { index -> remindSelectedIndex = index },
                onExpandedChange = { remindIntervalExpanded = it },
                supportingText = {
                    Row {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Remind interval info"
                        )
                        Text(
                            text = "When reminded of goals, this will determine how frequently this goal is reminded. " +
                                    "This interval begins the day this goal is created."
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            var reflectionIntervalExpanded by remember {
                mutableStateOf(false)
            }
            var selectedReflectionIntervalIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
            val reflectionIntervals = GoalInterval.defaultList

            TextFieldDropdown(
                label = stringResource(id = R.string.create_reflection_frequency),
                expanded = reflectionIntervalExpanded,
                menuHeight = 150.dp,
                selectedIndex = selectedReflectionIntervalIndex,
                items = reflectionIntervals.map { it.name },
                onItemClicked = { index -> selectedReflectionIntervalIndex = index },
                onExpandedChange = { reflectionIntervalExpanded = it },
                supportingText = {
                    Row {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(id = R.string.create_reflection_frequency_info_desc)
                        )
                        Text(
                            text = stringResource(id = R.string.create_reflection_frequency_info)
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            CreateGoalButton(
                goalName = goalName,
                goalDescription = goalDescription,
                goalCompletionDate = datePickerState.selectedDateMillis ?: now.toEpochMilli(),
                goalInterval = reflectionIntervals[selectedReflectionIntervalIndex],
                addGoal = addGoal,
                goalManagerHandle = goalpostNav.goalManager,
                inputsValid = isNameValid && isDescriptionValid
            )
        }
    }
}
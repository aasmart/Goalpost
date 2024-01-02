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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold
import io.aasmart.goalpost.compose.components.DatePickerField
import io.aasmart.goalpost.compose.components.Dropdown
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalTimePeriod
import io.aasmart.goalpost.utils.InputUtils
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
private fun CreateGameButton(
    goalName: String,
    goalDescription: String,
    goalCompletionDate: Long,
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
                timePeriod = GoalTimePeriod("", 86400000L),
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
        Text(text = "Create Goal")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    goalpostNav: GoalpostNav,
    addGoal: suspend (goal: Goal) -> Unit,
) {
    val context = LocalContext.current

    val goalNameMinLength = 1
    val goalNameMaxLength = 32

    val descriptionMinLength = 1

    var goalName by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }

    val isNameValid = InputUtils.isValidLength(goalName.trim(), goalNameMinLength, goalNameMaxLength)
    val isDescriptionValid = InputUtils.isValidLength(goalDescription.trim(), descriptionMinLength)

    var timePeriodDialogVisible by remember { mutableStateOf(false) }

    GoalpostNavScaffold(nav = goalpostNav) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .padding(it)
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
                    Text(text = "Goal Name")
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                isError = !isNameValid,
                supportingText = {
                    if(!isNameValid)
                        Text(text = "Name must be between $goalNameMinLength and $goalNameMaxLength characters")
                    else
                        Text(text = "${goalName.trim().length}/${goalNameMaxLength} characters")
                }
            )

            // Description Input
            OutlinedTextField(
                value = goalDescription,
                onValueChange = { goalDescription = it },
                label = {
                    Text(text = "Goal Description")
                },
                isError = !isDescriptionValid,
                supportingText = {
                    if(!isDescriptionValid)
                        Text(text = "Description must be at least $descriptionMinLength character(s) in length")
                    else
                        Text(text = "${goalDescription.trim().length} character(s)")
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

            DatePickerField(
                label = "Goal Completion Date",
                currentTime = now,
                datePickerState,
                datePickerExpanded,
                { datePickerExpanded = it },
                { it >= now.truncatedTo(ChronoUnit.DAYS).toEpochMilli() }
            )

            var remindIntervalExpanded by remember {
                mutableStateOf(false)
            }
            var remindSelectedIndex by remember {
                mutableIntStateOf(0)
            }

            Dropdown(
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
            var reflectionSelectedIndex by remember {
                mutableIntStateOf(0)
            }

            Dropdown(
                label = "Reflection Interval",
                expanded = reflectionIntervalExpanded,
                menuHeight = 150.dp,
                selectedIndex = reflectionSelectedIndex,
                items = listOf("Daily", "Weekly", "Bi-Weekly", "Monthly", "Custom"),
                onItemClicked = { index -> reflectionSelectedIndex = index },
                onExpandedChange = { reflectionIntervalExpanded = it },
                supportingText = {
                    Row {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Remind interval info"
                        )
                        Text(
                            text = "When it's time to reflect on goals, this will determine how frequently this goal needs reflections. " +
                                    "This interval begins the day this goal is created."
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            CreateGameButton(
                goalName = goalName,
                goalDescription = goalDescription,
                goalCompletionDate = datePickerState.selectedDateMillis ?: now.toEpochMilli(),
                addGoal = addGoal,
                goalManagerHandle = goalpostNav.goalManager,
                inputsValid = isNameValid && isDescriptionValid
            )
        }
    }
}
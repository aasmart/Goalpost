package io.aasmart.goalpost.src.compose.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.src.compose.components.DatePickerField
import io.aasmart.goalpost.src.compose.components.Dropdown
import io.aasmart.goalpost.src.goals.models.Goal
import io.aasmart.goalpost.src.goals.models.GoalTimePeriod
import io.aasmart.goalpost.src.utils.InputUtils
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
private fun CreateGameButton(
    goalName: String,
    goalDescription: String,
    addGoal : suspend (goal: Goal) -> Unit,
    goalManagerHandle: () -> Unit,
    inputsValid: Boolean
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            val goal = Goal(
                title = goalName,
                description = goalDescription,
                timePeriod = GoalTimePeriod("", 0)
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

private val weekdays = setOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    scaffoldPadding: PaddingValues,
    addGoal: suspend (goal: Goal) -> Unit,
    goalManagerHandle: () -> Unit
) {
    val context = LocalContext.current

    val goalNameMinLength = 1
    val goalNameMaxLength = 32

    val descriptionMinLength = 1

    var goalName by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val isNameValid = InputUtils.isValidLength(goalName.trim(), goalNameMinLength, goalNameMaxLength)
    val isDescriptionValid = InputUtils.isValidLength(goalDescription.trim(), descriptionMinLength)

    var timePeriodDialogVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .padding(scaffoldPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(top = 6.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Dropdown(
                label = "Set Goal Reminder Period",
                expanded = expanded,
                menuHeight = 150.dp,
                selectedIndex = selectedIndex,
                items = listOf("Daily", "Weekly", "Bi-Weekly", "Monthly"),
                onItemClicked = { index -> selectedIndex = index },
                onExpandedChange = { expanded = it },
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.85f)
            )

            OutlinedIconButton(
                onClick = { timePeriodDialogVisible = true },
                shape = RoundedCornerShape(4.dp),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.Edit,
                    "Create new time configuration",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // End time date picker

        val now = Instant.now()
        var datePickerExpanded by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        DatePickerField(
            currentTime = now,
            datePickerState,
            datePickerExpanded,
            { datePickerExpanded = it },
            { it >= now.truncatedTo(ChronoUnit.DAYS).toEpochMilli() }
        )


        CreateGameButton(
            goalName = goalName,
            goalDescription = goalDescription,
            addGoal = addGoal,
            goalManagerHandle = goalManagerHandle,
            inputsValid = isNameValid && isDescriptionValid
        )
    }
}
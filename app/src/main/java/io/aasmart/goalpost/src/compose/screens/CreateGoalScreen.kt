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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.src.compose.components.Dropdown
import io.aasmart.goalpost.src.goals.models.Goal
import io.aasmart.goalpost.src.goals.models.GoalTimePeriod
import kotlinx.coroutines.launch

@Composable
private fun CreateGameButton(
    goalName: String,
    goalDescription: String,
    addGoal : suspend (goal: Goal) -> Unit,
    goalManagerHandle: () -> Unit
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
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(text = "Create Goal")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    scaffoldPadding: PaddingValues,
    addGoal: suspend (goal: Goal) -> Unit,
    goalManagerHandle: () -> Unit
) {
    var goalName by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .padding(scaffoldPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = goalName,
            onValueChange = { goalName = it },
            label = {
                Text(text = "Goal Name")
            },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )

        OutlinedTextField(
            value = goalDescription,
            onValueChange = { goalDescription = it },
            label = {
                Text(text = "Goal Description")
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
                onClick = {},
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

        CreateGameButton(
            goalName = goalName,
            goalDescription = goalDescription,
            addGoal = addGoal,
            goalManagerHandle = goalManagerHandle
        )
    }
}
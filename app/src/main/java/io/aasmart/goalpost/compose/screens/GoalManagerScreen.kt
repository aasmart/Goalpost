package io.aasmart.goalpost.compose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold
import io.aasmart.goalpost.goals.models.Goal
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IncompleteGoalReflectionCard(
    goal: Goal,
    cardHeight: Dp = 70.dp,
    calendarScreenNav: () -> Unit,
    manageGoalNav: (Goal) -> Unit,
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable {
                manageGoalNav(goal)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = goal.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )

            val dateTooltipState = remember { RichTooltipState() }
            RichTooltipBox(
                title = { Text("Goal Dates") },
                tooltipState = dateTooltipState,
                text = {
                    val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(goal.completionDate), ZoneId.of("UTC"))
                    val dateString =
                        "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} " +
                        "${date.dayOfMonth}, ${date.year}"

                    Text("You want to accomplish ${goal.title} by $dateString")
                }
            ) {
                IconButton(
                    onClick = calendarScreenNav,
                    modifier = Modifier
                        .fillMaxHeight(.6f)
                        .aspectRatio(1f)
                        .tooltipAnchor()
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "View end date",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = { manageGoalNav(goal) },
                modifier = Modifier
                    .fillMaxHeight(.6f)
                    .aspectRatio(1f),
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "View goal",
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GoalsManager(
    goalpostNav: GoalpostNav,
    manageGoalNav: (Goal) -> Unit,
    goals: List<Goal>
) {
    var completeGoalScreen by rememberSaveable {
        mutableStateOf(false)
    }

    GoalpostNavScaffold(nav = goalpostNav) { padding ->
        if(goals.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("You don't have any goals set.")
                Button(onClick = goalpostNav.createGoal) {
                    Text("Set Goals")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // Tab buttons for complete/incomplete goals
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { completeGoalScreen = false },
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = (
                                if(!completeGoalScreen)
                                    MaterialTheme.colorScheme.background
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                            ),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(id = R.string.in_progress_goals))
                    }
                    Button(
                        onClick = { completeGoalScreen = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = (
                                if(completeGoalScreen)
                                    MaterialTheme.colorScheme.background
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                            ),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(id = R.string.completed_goals))
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        goals.filter { it.isCompleted() == completeGoalScreen }
                    ) { _, goal ->
                        IncompleteGoalReflectionCard(
                            goal,
                            calendarScreenNav = goalpostNav.goalCalendar,
                            manageGoalNav = manageGoalNav
                        )
                    }
                }
            }
        }
    }
}
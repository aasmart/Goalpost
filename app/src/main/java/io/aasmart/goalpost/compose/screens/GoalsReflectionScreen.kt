package io.aasmart.goalpost.compose.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.models.Goal
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
private fun IncompleteGoalReflectionCard(
    goal: Goal,
    cardHeight: Dp = 60.dp,
    navGoalReflection: (goal: Goal) -> Unit
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                text = goal.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )

            TextButton(
                onClick = { navGoalReflection(goal) },
            ) {
                Text(text = stringResource(id = R.string.reflect))
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(id = R.string.reflect),
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }
}

@Composable
private fun CompleteGoalReflectionCard(
    goal: Goal,
    cardHeight: Dp = 60.dp,
    navGoalReflection: (goal: Goal) -> Unit
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                text = goal.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            TextButton(
                onClick = { navGoalReflection(goal) },
            ) {
                Text(text = stringResource(id = R.string.view))
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(id = R.string.reflect),
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }
}

@Composable
private fun FinishReflectionFAB(
    enabled: Boolean,
    finishGoalReflection: () -> Unit
) {
    val context = LocalContext.current
    val containerColor = MaterialTheme
        .colorScheme
        .primaryContainer
        .copy(alpha = if(enabled) 1f else 0.5f)
    val contentColor = MaterialTheme
        .colorScheme
        .onPrimaryContainer
        .copy(alpha = if(enabled) 1f else 0.4f)

    ExtendedFloatingActionButton(
        onClick = {
            if(enabled)
                finishGoalReflection()
            else {
                Toast.makeText(
                    context,
                    context.getString(R.string.reflection_incomplete_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp
        ),
        containerColor = containerColor,
        contentColor = contentColor,
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = stringResource(id = R.string.complete_reflection)
        )
        Text(text = stringResource(id = R.string.complete_reflection))
    }
}

@Composable
fun GoalsReflectionScreen(
    goals: List<Goal>,
    navGoalReflection: (goal: Goal) -> Unit,
    backNav: () -> Unit,
    homeNav: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dayBeginInstant = Instant.now()

    // Retrieve goals that only require reflections today
    val reflectionGoals = goals.map {
        it to it.getCurrentReflection(dayBeginInstant)
    }.filter {
        it.second != null
    }.toMap()

    // Split the goals into incomplete and complete reflections
    val incompleteReflections = reflectionGoals
        .filter { it.value?.isCompleted == false }
        .map { it.key }
    val completeReflections = reflectionGoals
        .filter { it.value?.isCompleted == true }
        .map { it.key }

    Scaffold(
        floatingActionButton = {
            FinishReflectionFAB(
                enabled = incompleteReflections.isEmpty()
            ) {
                coroutineScope.launch {
                    context.settingsDataStore.updateData {
                        return@updateData it.toBuilder()
                            .setNeedsToReflect(false)
                            .setLastCompletedReflection(Instant.now().toEpochMilli())
                            .build()
                    }
                    try { backNav() } catch (_: Exception) { homeNav() }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (goals.isEmpty())
                Text(text = stringResource(id = R.string.no_goals_to_reflect))
            else {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (incompleteReflections.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.incomplete_reflections),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        items(incompleteReflections) { goal ->
                            IncompleteGoalReflectionCard(
                                goal = goal,
                                navGoalReflection = navGoalReflection
                            )
                        }
                    }

                    if (completeReflections.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.complete_reflections),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        items(completeReflections) { goal ->
                            CompleteGoalReflectionCard(
                                goal = goal,
                                navGoalReflection = navGoalReflection
                            )
                        }
                    }
                }
            }
        }   
    }
}
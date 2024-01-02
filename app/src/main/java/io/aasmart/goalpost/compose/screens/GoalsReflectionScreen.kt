package io.aasmart.goalpost.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import io.aasmart.goalpost.ui.NoRippleTheme
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

            OutlinedButton(onClick = { navGoalReflection(goal) }) {
                Text(text = stringResource(id = R.string.reflect))
            }
        }
    }
}

@Composable
private fun CompleteGoalReflectionCard(
    goal: Goal,
    cardHeight: Dp = 60.dp,
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
            Icon(
                Icons.Filled.Check,
                modifier = Modifier
                    .weight(.1f)
                    .aspectRatio(1f),
                contentDescription = "Goal reflection completed",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = goal.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun FinishReflectionFAB(
    enabled: Boolean,
    finishGoalReflection: () -> Unit
) {
    val containerColor = MaterialTheme
        .colorScheme
        .primaryContainer
        .copy(alpha = if(enabled) 1f else 0.5f)
    val contentColor = MaterialTheme
        .colorScheme
        .onPrimaryContainer
        .copy(alpha = if(enabled) 1f else 0.4f)

    val ripple: RippleTheme = if(enabled) LocalRippleTheme.current else NoRippleTheme
    CompositionLocalProvider(LocalRippleTheme provides ripple) {
        ExtendedFloatingActionButton(
            onClick = {
                if(enabled) finishGoalReflection() else return@ExtendedFloatingActionButton
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

    // Get the beginning of the current day
    val dayBeginInstant = Instant.now()
        .truncatedTo(ChronoUnit.DAYS)

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
                        return@updateData it.toBuilder().setNeedsToReflect(false).build()
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
            if (goals.isEmpty()) {
                Text(text = stringResource(id = R.string.no_goals_to_reflect))
                Button(onClick = {}) {
                    Text(stringResource(id = R.string.complete_reflection))
                }
                return@Column
            }

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(incompleteReflections.isNotEmpty()) {
                    item { Text(text = "Incomplete Goal Reflections") }
                    items(incompleteReflections) { goal ->
                        IncompleteGoalReflectionCard(
                            goal = goal,
                            navGoalReflection = navGoalReflection
                        )
                    }
                }

                if(completeReflections.isNotEmpty()) {
                    item { Text(text = "Completed Goal Reflections") }
                    items(completeReflections) { goal ->
                        CompleteGoalReflectionCard(goal)
                    }
                }
            }
        }   
    }
}
package io.aasmart.goalpost.compose.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.goals.models.Goal
import kotlinx.coroutines.flow.Flow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalReflectionScreen(
    goalId: String,
    getGoals: (Context) -> Flow<List<Goal>>,
    navBack: () -> Unit
) {
    val goals = getGoals(LocalContext.current).collectAsState(initial = null).value
    val goal = goals?.find { goal -> goal.id == goalId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = goal?.title ?: "")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {

        }
    }
}

@Composable
fun GoalsReflectionScreen(
    goals: List<Goal>,
    navGoalReflection: (goal: Goal) -> Unit
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (goals.isEmpty()) {
            Button(onClick = {}) {
                Text("Finish Reflection")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item { Text(text = "Incomplete Goal Reflections") }
                itemsIndexed(incompleteReflections) { index, goal ->
                    IncompleteGoalReflectionCard(
                        goal = goal,
                        navGoalReflection = navGoalReflection
                    )
                }

                item { Text(text = "Completed Goal Reflections") }
                itemsIndexed(completeReflections) { index, goal ->
                    CompleteGoalReflectionCard(goal)
                }
            }
        }
    }
}
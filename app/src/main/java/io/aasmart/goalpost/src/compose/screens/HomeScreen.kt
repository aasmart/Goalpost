package io.aasmart.goalpost.src.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.src.goals.models.Goal

@Composable
fun Greeting(name: String = "Person") {
    Text(text = "${stringResource(id = R.string.salutation)}, $name", fontSize = 48.sp)
}

@Composable
fun GoalCard(goal: Goal) {
    Column(modifier = Modifier
        .background(Color.Black.copy(alpha = 0.075F), RoundedCornerShape(6.dp))
        .fillMaxWidth()
        .padding(2.dp)
    ) {
        Text(
            text = goal.title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        )
        Text(text = goal.description)
    }
}

@Composable
fun GoalsSnippetCard(
    goals: Array<Goal>,
    displayNumGoals: Int = 2,
    goalManagerHandle: () -> Unit,
    createGoalHandle: () -> Unit
) {
    val selectedGoals = goals
        .asSequence()
        .shuffled()
        .take(displayNumGoals)
        .toList()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(.95f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if(selectedGoals.isEmpty()) {
                Text(stringResource(id = R.string.set_goals_reminder))

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.small,
                    onClick = { createGoalHandle() }
                ) {
                    Text(text = stringResource(R.string.set_goals))
                }

                return@ElevatedCard
            }

            Text(
                text = stringResource(id = R.string.current_goal_snippet),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                contentPadding = PaddingValues(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(selectedGoals) {
                    GoalCard(goal = it)
                }

                item {
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.small,
                        onClick = { goalManagerHandle() }
                    ) {
                        Text(text = stringResource(R.string.view_goals))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    scaffoldPadding: PaddingValues,
    createGoalHandle: () -> Unit,
    goalManagerHandle: () -> Unit,
    goals: Array<Goal>
) {
    Column(modifier = Modifier.padding(scaffoldPadding).fillMaxSize()) {
        Greeting()
        GoalsSnippetCard(
            goals,
            2,
            goalManagerHandle,
            createGoalHandle
        )
    }
}
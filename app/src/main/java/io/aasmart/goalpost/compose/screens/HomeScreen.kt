package io.aasmart.goalpost.compose.screens

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
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold
import io.aasmart.goalpost.goals.models.Goal

@Composable
fun Greeting(name: String = "Person") {
    Text(text = "${stringResource(id = R.string.salutation)}, $name", fontSize = 48.sp)
}

@Composable
private fun IncompleteGoalReflectionCard(goal: Goal) {
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
    displayNumGoals: Int = 3,
    goalManagerHandle: () -> Unit,
    createGoalHandle: () -> Unit
) {
    val selectedGoals = goals
        .sortedBy { it.completionDate }
        .take(displayNumGoals)

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
                    IncompleteGoalReflectionCard(goal = it)
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
    goalpostNav: GoalpostNav,
    goals: Array<Goal>
) {
    GoalpostNavScaffold(nav = goalpostNav) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
        ) {
            Greeting()
            GoalsSnippetCard(
                goals,
                3,
                goalpostNav.goalManager,
                goalpostNav.createGoal
            )
        }
    }
}
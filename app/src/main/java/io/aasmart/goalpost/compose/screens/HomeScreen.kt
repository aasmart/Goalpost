package io.aasmart.goalpost.compose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold
import io.aasmart.goalpost.goals.models.Goal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun Greeting(name: String = "Person") {
    Column {
        Text(
            text = "${stringResource(id = R.string.salutation)}, $name",
            fontSize = 36.sp
        )
    }
}

@Composable
private fun GoalCardItem(
    goal: Goal,
    manageGoalNav: (Goal) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            manageGoalNav(goal)
        }
    ) {
        Text(
            text = goal.title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = MaterialTheme.typography.titleLarge .fontSize
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
    createGoalHandle: () -> Unit,
    manageGoalNav: (Goal) -> Unit
) {
    val selectedGoals = goals
        .filter { !it.isCompleted() }
        .sortedBy { it.completionDate }
        .take(displayNumGoals)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if(selectedGoals.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.set_goals_reminder),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
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

            LazyColumn(
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(selectedGoals) {
                    GoalCardItem(goal = it, manageGoalNav = manageGoalNav)
                }

                item {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
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
private fun NextReflectionCard(
    goals: Array<Goal>,
    goalReflectionTimeMillis: Long,
) {
    if(goals.isEmpty())
        return

    val nextGoalReflectionDay = goals.filter {
        !it.isCompleted()
    }.minOfOrNull {
        it.reflections.filter { ref -> !ref.isCompleted }
            .minOfOrNull { ref -> ref.dateTimeMillis }
            ?: Long.MAX_VALUE
    } ?: return

    val localDateTime = Instant
        .ofEpochMilli(nextGoalReflectionDay)
        .atZone(ZoneId.systemDefault())
        .with(ChronoField.MILLI_OF_DAY, 0)
        .plus(goalReflectionTimeMillis, ChronoUnit.MILLIS)
    val dateString =
            "${localDateTime.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())} " +
            "${localDateTime.dayOfMonth}" +
            ", ${localDateTime.year} at " +
            localDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(text = stringResource(id = R.string.next_scheduled_reflection))
                append(" ")
                pushStyle(SpanStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline
                ))
                append(dateString)
                append(".")
                toAnnotatedString()
            },
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun HomeScreen(
    goalpostNav: GoalpostNav,
    goals: Array<Goal>,
    preferredName: String,
    goalReflectionTimeMillis: Long,
    manageGoalNav: (Goal) -> Unit
) {
    GoalpostNavScaffold(nav = goalpostNav) {
        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Greeting(preferredName)
                GoalsSnippetCard(
                    goals,
                    3,
                    goalpostNav.goalManager,
                    goalpostNav.createGoal,
                    manageGoalNav
                )
                NextReflectionCard(goals, goalReflectionTimeMillis)
            }
        }
    }
}
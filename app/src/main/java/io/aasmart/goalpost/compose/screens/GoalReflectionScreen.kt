package io.aasmart.goalpost.compose.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.aasmart.goalpost.R
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalReflection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant

private suspend fun completeReflection(
    context: Context,
    goal: Goal,
    goalReflection: GoalReflection,
    setGoal: suspend (Context, Goal) -> Unit
) {
    val updatedGoal = goal.let {
        val reflectionIndex =
            it.reflections.indexOfFirst { ref -> ref.dateTimeMillis == goalReflection.dateTimeMillis }
        val updatedReflections = it.reflections.toMutableList().apply {
            this[reflectionIndex] = goalReflection.copy(
                isCompleted = true
            )
        }

        return@let it.copy(
            reflections = updatedReflections,
            id = it.id
        )
    }

    setGoal(context, updatedGoal)
}

@Composable
private fun GoalReflectionForm(
    goal: Goal,
    goalReflection: GoalReflection,
    setGoal: suspend (Context, Goal) -> Unit,
    padding: PaddingValues,
    navBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    completeReflection(
                        context,
                        goal,
                        goalReflection,
                        setGoal
                    )
                    navBack()
                }
            }
        ) {
            Text(text = stringResource(id = R.string.complete_reflection))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalReflectionScreen(
    goalId: String,
    getGoals: (Context) -> Flow<List<Goal>>,
    setGoal: suspend (Context, Goal) -> Unit,
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
    ) { padding ->
        val reflection = goal?.getCurrentReflection(Instant.now())

        if(reflection == null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text(text = stringResource(id = R.string.failed_goal_load))
                Button(onClick = navBack) {
                    Text(text = stringResource(id = R.string.return_to_reflections))
                }
            }
            return@Scaffold
        }

        GoalReflectionForm(
            goal = goal,
            goalReflection = reflection,
            setGoal = setGoal,
            padding = padding,
            navBack = navBack
        )
    }
}
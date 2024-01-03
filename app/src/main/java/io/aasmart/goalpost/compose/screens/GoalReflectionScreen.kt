package io.aasmart.goalpost.compose.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.components.GoalpostSlider
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalReflection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Updates a goal in the goal database with a completed goal reflection
 *
 * @param goal The goal the goal reflection belongs to
 * @param updatedGoalReflection The updated version of the original
 *      goal reflection. This should have the same dateTimeMillis
 *      as the goal reflection being replaced.
 * @param context Th current context
 * @param setGoal A function to update a goal in the goals database
 * */
private suspend fun updateReflection(
    goal: Goal,
    updatedGoalReflection: GoalReflection,
    context: Context,
    setGoal: suspend (Context, Goal) -> Unit
) {
    val updatedGoal = goal.let {
        // Update reflection in goal's reflection list
        val reflectionIndex =
            it.reflections.indexOfFirst { ref -> ref.dateTimeMillis == updatedGoalReflection.dateTimeMillis }
        val updatedReflections = it.reflections.toMutableList().apply {
            this[reflectionIndex] = updatedGoalReflection
        }

        return@let it.copy(
            reflections = updatedReflections,
            id = it.id
        )
    }

    setGoal(context, updatedGoal)
}

@Composable
private fun FieldHeader(
    title: String,
    subTitle: String
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subTitle,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Light
        )
    }
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
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val readOnly = goalReflection.isCompleted

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(padding)
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        /* ==========================
        * Intro text
        *  ==========================*/
        Column {
            Text(
                text = stringResource(id = R.string.reflection_intro_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(id = R.string.reflection_insight),
                fontWeight = FontWeight.Light
            )
        }

        /* ==========================
        * Made progress slider
        *  ==========================*/
        var workedTowardsGoalSliderValue by rememberSaveable {
            mutableFloatStateOf(goalReflection.madeProgress ?: 2f)
        }

        GoalpostSlider(
            label = {
                Text(
                    text = stringResource(id = R.string.reflection_made_goal_progress),
                    fontWeight = FontWeight.Bold
                )
            },
            value = workedTowardsGoalSliderValue,
            onValueChange = { workedTowardsGoalSliderValue = it },
            valueRange = 0f..4f,
            steps = 3,
            stepLabels = mapOf(
                0 to stringResource(id = R.string.disagree),
                2 to stringResource(id = R.string.neutral),
                4 to stringResource(id = R.string.agree)
            ),
            enabled = !readOnly
        )

        /* ==========================
        * Reflection on progress made
        *  ==========================*/
        var progressTextAnswer by rememberSaveable {
            mutableStateOf(goalReflection.madeProgressReflection ?: "")
        }

        FieldHeader(
            title = stringResource(id = R.string.reflection_previous_answer),
            subTitle = stringResource(id = R.string.reflection_made_progress_text)
        )
        TextField(
            value = progressTextAnswer, 
            onValueChange = { progressTextAnswer = it },
            minLines = 8,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.reflections_made_progress_placeholder)
                )
            },
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
        )

        /* ==========================
        * "Can I do better?" slider
        *  ==========================*/
        var canDoBetterSlideValue by rememberSaveable {
            mutableFloatStateOf(goalReflection.couldDoBetter ?: 2f)
        }

        GoalpostSlider(
            label = {
                Text(
                    text = stringResource(id = R.string.reflection_can_do_better),
                    fontWeight = FontWeight.Bold
                )
            },
            value = canDoBetterSlideValue,
            onValueChange = { canDoBetterSlideValue = it },
            valueRange = 0f..4f,
            steps = 3,
            stepLabels = mapOf(
                0 to stringResource(id = R.string.disagree),
                2 to stringResource(id = R.string.neutral),
                4 to stringResource(id = R.string.agree)
            ),
            enabled = !readOnly
        )

        /* ==========================
        * "Can I do better?" text reflection
        *  ==========================*/
        var moreToAccomplishText by rememberSaveable {
            mutableStateOf(goalReflection.couldDoBetterReflection ?: "")
        }

        FieldHeader(
            title = stringResource(id = R.string.reflection_previous_answer),
            subTitle = stringResource(id = R.string.reflections_can_do_better_text)
        )
        TextField(
            value = moreToAccomplishText,
            onValueChange = { moreToAccomplishText = it },
            minLines = 8,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.reflections_can_do_better_placeholder)
                )
            },
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
        )

        /* ==========================
        * "Can I do better?" text reflection
        *  ==========================*/
        var changesToImproveText by rememberSaveable {
            mutableStateOf(goalReflection.stepsToImprove ?: "")
        }

        FieldHeader(
            title = stringResource(id = R.string.reflections_changes_to_improve_title),
            subTitle = stringResource(id = R.string.reflections_changes_to_improve_text)
        )
        TextField(
            value = changesToImproveText,
            onValueChange = { changesToImproveText = it },
            minLines = 8,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.reflections_changes_to_improve_placeholder)
                )
            },
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
        )

        if(!readOnly) {
            /* ==========================
            * Finish reflection button
            *  ==========================*/
            OutlinedButton(
                onClick = {
                    val updatedGoal = goalReflection.copy(
                        isCompleted = true,
                        madeProgress = workedTowardsGoalSliderValue,
                        madeProgressReflection = progressTextAnswer,
                        couldDoBetter = canDoBetterSlideValue,
                        couldDoBetterReflection = moreToAccomplishText,
                        stepsToImprove = changesToImproveText
                    )

                    coroutineScope.launch {
                        updateReflection(
                            context = context,
                            goal = goal,
                            updatedGoalReflection = updatedGoal,
                            setGoal = setGoal
                        )
                        navBack()
                    }
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.complete_reflection))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReflectionTopAppBar(
    goal: Goal?,
    navBack: () -> Unit
) {
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
        topBar = { ReflectionTopAppBar(goal = goal, navBack) }
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
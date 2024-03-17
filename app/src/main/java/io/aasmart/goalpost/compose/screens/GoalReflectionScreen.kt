package io.aasmart.goalpost.compose.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.components.FieldHeader
import io.aasmart.goalpost.compose.components.GoalpostSlider
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalReflection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
private suspend fun updateGoal(
    goal: Goal,
    completedGoal: Boolean?,
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
            accomplishedGoal = completedGoal,
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
    navBack: () -> Unit,
    updateIsDirty: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val readOnly = goalReflection.isCompleted
    val isFinalReflection = goal.reflections.last() == goalReflection

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
            onValueChange = {
                workedTowardsGoalSliderValue = it
                updateIsDirty()
            },
            valueRange = GoalReflection.SLIDER_MIN_VAL..GoalReflection.SLIDER_MAX_VAL,
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
            onValueChange = {
                progressTextAnswer = it
                updateIsDirty()
            },
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
            onValueChange = {
                canDoBetterSlideValue = it
                updateIsDirty()
            },
            valueRange = GoalReflection.SLIDER_MIN_VAL..GoalReflection.SLIDER_MAX_VAL,
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
            onValueChange = {
                moreToAccomplishText = it
                updateIsDirty()
            },
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
            onValueChange = {
                changesToImproveText = it
                updateIsDirty()
            },
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

        var completedGoal by remember {
            mutableStateOf(goal.accomplishedGoal)
        }

        if(isFinalReflection) {
            FieldHeader(
                title = stringResource(id = R.string.reflection_accomplished_title),
                subTitle = stringResource(id = R.string.reflection_accomplished_description)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        completedGoal = true
                        updateIsDirty()
                    },
                    shape = RoundedCornerShape(4.dp),
                    enabled = completedGoal == null || completedGoal == true,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(id = R.color.light_green)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null
                    )
                    Text(text = stringResource(id = R.string.reflection_accomplished))
                }
                OutlinedButton(
                    onClick = {
                        completedGoal = false
                        updateIsDirty()
                    },
                    shape = RoundedCornerShape(4.dp),
                    enabled = completedGoal == null || completedGoal == false,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null
                    )
                    Text(text = stringResource(id = R.string.reflection_didnt_accomplished))
                }
            }
        }

        if(!readOnly) {
            FieldHeader(
                title = stringResource(id = R.string.reflection_reached_end),
                subTitle = stringResource(id = R.string.reflection_reached_end_subtitle)
            )

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
                        navBack()
                        updateGoal(
                            context = context,
                            goal = goal,
                            updatedGoalReflection = updatedGoal,
                            setGoal = setGoal,
                            completedGoal = completedGoal
                        )
                    }
                },
                shape = RoundedCornerShape(4.dp),
                enabled = !(isFinalReflection && completedGoal == null),
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
    reflectionCompleted: Boolean,
    isDirty: Boolean,
    setConfirmExitDialogVisible: () -> Unit,
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
            IconButton(
                onClick = {
                    if(reflectionCompleted)
                        navBack()
                    else {
                        if(!isDirty)
                            navBack()
                        else
                            setConfirmExitDialogVisible()
                    }
                }
            ) {
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
private fun ConfirmExitDialog(
    onVisibleChange: (Boolean) -> Unit,
    navBack: () -> Unit
) {
    AlertDialog(onDismissRequest = { onVisibleChange(false) }) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.reflection_exit_confirm),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { onVisibleChange(false) },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
                OutlinedButton(
                    onClick = {
                        onVisibleChange(false)
                        navBack()
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    }
}

@Composable
fun GoalReflectionScreen(
    goalId: String,
    goalReflectionId: String,
    getGoals: (Context) -> Flow<List<Goal>>,
    setGoal: suspend (Context, Goal) -> Unit,
    navBack: () -> Unit
) {
    val goals = getGoals(LocalContext.current).collectAsState(initial = null).value
    val goal = goals?.find { goal -> goal.id == goalId }
    val reflection = goal?.reflections?.find { it.id == goalReflectionId }
    var isDirty by rememberSaveable {
        mutableStateOf(false)
    }

    var showConfirmExitDialog by remember {
        mutableStateOf(false)
    }

    if(showConfirmExitDialog)
        ConfirmExitDialog(onVisibleChange = { showConfirmExitDialog = it }, navBack)

    Scaffold(
        topBar = {
            ReflectionTopAppBar(
                goal = goal,
                reflectionCompleted = reflection?.isCompleted == true,
                setConfirmExitDialogVisible = { showConfirmExitDialog = true },
                navBack = navBack,
                isDirty = isDirty
            )
        }
    ) { padding ->
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
            navBack = navBack,
            updateIsDirty = { isDirty = true }
        )
    }
}
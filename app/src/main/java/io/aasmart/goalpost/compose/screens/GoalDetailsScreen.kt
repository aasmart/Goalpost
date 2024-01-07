package io.aasmart.goalpost.compose.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.aasmart.goalpost.IS_GOAL_EDITING_ENABLED
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.LoadingWheel
import io.aasmart.goalpost.compose.components.OutlinedTextFieldDatePicker
import io.aasmart.goalpost.compose.components.OutlinedTextFieldDropdown
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalInterval
import io.aasmart.goalpost.goals.models.GoalReflection
import io.aasmart.goalpost.utils.ColorUtils
import io.aasmart.goalpost.utils.InputUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
private fun GoalReflectionCalendarDay(
    goalReflection: GoalReflection?,
    day: Int
) {
    val color =
        if(goalReflection != null) {
            if(
                !goalReflection.isCompleted
                && Instant.now().toEpochMilli() > goalReflection.dateTimeMillis
            )
                MaterialTheme.colorScheme.errorContainer
            else if(goalReflection.isCompleted) {
                ColorUtils.lerp(
                    start = MaterialTheme.colorScheme.error,
                    stop = colorResource(id = R.color.light_green),
                    amount = goalReflection.madeProgress
                        ?.div((GoalReflection.SLIDER_MIN_VAL + GoalReflection.SLIDER_MAX_VAL))
                        ?: 0f
                )
            }
            else
                MaterialTheme.colorScheme.primary
        } else
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)

    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .aspectRatio(1f)
    ) {
        Text(
            text = day.toString(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GoalReflectionCalendar(
    goal: Goal,
) {
    var zonedNow by rememberSaveable {
        mutableStateOf(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
    }
    val firstDayDateTime = zonedNow
        .withDayOfMonth(1)
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    val firstDayOfWeek = firstDayDateTime.dayOfWeek.value
    val month = zonedNow.month

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.height(420.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                zonedNow = zonedNow.minusMonths(1)
            }) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(1f)
                )
            }

            Text(
                text =
                    "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${zonedNow.year}",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                zonedNow = zonedNow.plusMonths(1)
            }) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }

        val weekLetters = DateFormatSymbols(Locale.getDefault())
            .weekdays
            .filter { it.isNotEmpty() }
            .map { it[0].toString() }

        val dateTimeGoalReflectionMap = goal.reflections
            .associateBy { it.dateTimeMillis }

        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            items(weekLetters) {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
            items(firstDayOfWeek) {
                Box {}
            }
            items(month.maxLength()) {
                val dayMillis = firstDayDateTime
                    .toInstant()
                    .plusMillis((it) * 24 * 60 * 60 * 1000L)
                    .toEpochMilli()

                GoalReflectionCalendarDay(
                    dateTimeGoalReflectionMap[dayMillis],
                    day = (it + 1)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopAppBar(
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

@Composable
private fun EditFloatingActionButton(
    onClicked: () -> Unit
) {
    FloatingActionButton(onClick = onClicked) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = stringResource(id = R.string.edit_goal)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDisplay(
    goal: Goal,
    goalName: String,
    isGoalNameValid: Boolean,
    updateGoalName: (String) -> Unit,
    goalDescription: String,
    isGoalDescriptionValid: Boolean,
    updateGoalDescription: (String) -> Unit,
    goalCompletionPickerState: DatePickerState,
    selectedReflectionFrequencyIndex: Int,
    updateSelectedReflectionFrequencyIndex: (Int) -> Unit,
    isEditing: Boolean
) {
    val scroll = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {
        // GOAL NAME FIELD

        OutlinedTextField(
            label = { Text(text = stringResource(id = R.string.goal_name)) },
            value = goalName,
            onValueChange = updateGoalName,
            readOnly = !isEditing,
            isError = !isGoalNameValid,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if(!isGoalNameValid)
                    Text(
                        text = stringResource(id = R.string.between_num_characters)
                            .replace(
                                "{LABEL}",
                                stringResource(id = R.string.goal_name)
                            )
                            .replace(
                                "{MIN}",
                                Goal.NAME_MIN_LENGTH.toString()
                            )
                            .replace(
                                "{MAX}",
                                Goal.NAME_MAX_LENGTH.toString()
                            )
                    )
                else
                    Text(
                        text = stringResource(id = R.string.num_characters)
                            .replace(
                                "{NUM_CHARACTERS}",
                                "${goalName.trim().length}/${Goal.NAME_MAX_LENGTH}"
                            )
                    )
            }
        )

        // GOAL DESCRIPTION FIELD

        OutlinedTextField(
            label = { Text(text = stringResource(id = R.string.goal_description)) },
            value = goalDescription,
            onValueChange = updateGoalDescription,
            readOnly = !isEditing,
            minLines = 8,
            modifier = Modifier.fillMaxWidth(),
            isError = !isGoalDescriptionValid,
            supportingText = {
                if(!isGoalDescriptionValid)
                    Text(
                        text = stringResource(id = R.string.at_least_num_characters)
                            .replace(
                                "{LABEL}",
                                stringResource(id = R.string.goal_description)
                            )
                            .replace(
                                "{MIN}",
                                Goal.DESCRIPTION_MIN_LENGTH.toString()
                            )
                    )
                else
                    Text(
                        text = stringResource(id = R.string.num_characters)
                            .replace(
                                "{NUM_CHARACTERS}",
                                goalDescription.trim().length.toString()
                            )
                    )
            }
        )

        // GOAL COMPLETION DATE PICKER
        val now = Instant.now()
        if(isEditing) {
            var goalCompletionDatePickerExpanded by remember {
                mutableStateOf(false)
            }
            OutlinedTextFieldDatePicker(
                label = stringResource(id = R.string.goal_completion_date),
                currentTime = now,
                goalCompletionPickerState,
                expanded = goalCompletionDatePickerExpanded,
                onExpandedChange = { goalCompletionDatePickerExpanded = it },
                dateValidator = { it >= now.truncatedTo(ChronoUnit.DAYS).toEpochMilli() }
            )
        } else {
            OutlinedTextFieldDatePicker(
                label = stringResource(id = R.string.goal_completion_date),
                currentTime = now,
                goalCompletionPickerState,
                expanded = false,
                onExpandedChange = {},
                dateValidator = { it >= now.truncatedTo(ChronoUnit.DAYS).toEpochMilli() }
            )
        }

        // REFLECTION INTERVAL DROPDOWN

        val reflectionIntervals = GoalInterval.defaultList
        if(isEditing) {
            var reflectionFrequencyExpanded by remember {
                mutableStateOf(false)
            }

            OutlinedTextFieldDropdown(
                label = stringResource(id = R.string.create_reflection_frequency),
                expanded = reflectionFrequencyExpanded,
                menuHeight = 150.dp,
                selectedIndex = selectedReflectionFrequencyIndex,
                items = reflectionIntervals.map { it.name },
                onItemClicked = { index: Int -> updateSelectedReflectionFrequencyIndex(index) },
                onExpandedChange = { reflectionFrequencyExpanded = it },
                supportingText = {
                    Row {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(
                                id = R.string.create_reflection_frequency_info_desc
                            )
                        )
                        Text(
                            text = stringResource(id = R.string.create_reflection_frequency_info)
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Display a normal outline if the user is not editing the goal
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.create_reflection_frequency)) },
                value = reflectionIntervals.map { it.name }[selectedReflectionFrequencyIndex],
                onValueChange = {},
                readOnly = true,
                supportingText = {
                    Row {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(
                                id = R.string.create_reflection_frequency_info_desc
                            )
                        )
                        Text(
                            text = stringResource(id = R.string.create_reflection_frequency_info)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = stringResource(id = R.string.reflections_calendar),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        GoalReflectionCalendar(goal = goal)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(
    goalpostNav: GoalpostNav,
    goalId: String,
    getGoals: (Context) -> Flow<List<Goal>>,
    setGoals: suspend (Context, Goal) -> Unit
) {
    /* IMPORTANT
    *
    * Editing goals is fully implemented and working (probably has some bugs),
    * but is disabled by default. I decided that editing goals goes against what I think
    * is a good idea. To enable goal editing, there is a global IS_GOAL_EDITING_ENABLED
    * field that can be changed to 'true'. This will allow a FAB to appear that will launch
    * editing for the user.
    *
    */

    val goals by getGoals(LocalContext.current).collectAsState(initial = null)
    val goal = goals?.find { goal -> goal.id == goalId }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isEditing by rememberSaveable {
        mutableStateOf(false)
    }
    var isSaving by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = { DetailsTopAppBar(goal = goal, navBack = goalpostNav.up) },
        floatingActionButton = {
            if(!isEditing && IS_GOAL_EDITING_ENABLED) {
                EditFloatingActionButton { isEditing = IS_GOAL_EDITING_ENABLED }
            }
        }
    ) { padding ->
        if(goal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Could not find goal")
                Button(onClick = goalpostNav.goalManager) {
                    Text(text = "Return to Goal Manager")
                }
            }
            return@Scaffold
        }

        if(isSaving) {
            LoadingWheel()
            return@Scaffold
        }

        // Put all goal form values out here because Compose
        var goalName by rememberSaveable {
            mutableStateOf(goal.title)
        }
        val isGoalNameValid = InputUtils.isValidLength(
            goalName.trim(),
            Goal.NAME_MIN_LENGTH,
            Goal.NAME_MAX_LENGTH
        )

        var goalDescription by rememberSaveable {
            mutableStateOf(goal.description)
        }
        val isGoalDescriptionValid = InputUtils.isValidLength(
            goalDescription.trim(),
            Goal.DESCRIPTION_MIN_LENGTH
        )

        var selectedReflectionIntervalIndex by remember {
            mutableIntStateOf(
                GoalInterval.defaultList.indexOfFirst {
                    goal.timePeriod.intervalMillis == it.intervalMillis
                }
            )
        }
        val goalCompletionDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = goal.completionDate
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoalDisplay(
                goal = goal,
                goalName = goalName,
                isGoalNameValid = isGoalNameValid,
                updateGoalName = { goalName = it },
                goalDescription = goalDescription,
                isGoalDescriptionValid = isGoalDescriptionValid,
                updateGoalDescription = { goalDescription = it },
                goalCompletionPickerState = goalCompletionDatePickerState,
                selectedReflectionFrequencyIndex = selectedReflectionIntervalIndex,
                updateSelectedReflectionFrequencyIndex = { selectedReflectionIntervalIndex = it },
                isEditing = isEditing
            )

            if(isEditing) {
                val saveButtonClick: () -> Unit = {
                    coroutineScope.launch {
                        // Show loading wheel
                        isSaving = true

                        val timePeriod = GoalInterval.defaultList[
                            selectedReflectionIntervalIndex
                        ]

                        /*
                        * Regenerates the reflections based on the new parameters.
                        * Preserves older reflections (including the one of the current day).
                        * This is why Goal#createReflectionFromDate has an additional time
                        * period added to it
                        */
                        val reflections = goal.reflections
                            .filter {
                                val now = ZonedDateTime
                                    .ofInstant(
                                        Instant.ofEpochMilli(goal.beginDate),
                                        ZoneId.of("UTC")
                                    ).with(ChronoField.MILLI_OF_DAY, 0)
                                    .toInstant()
                                    .toEpochMilli()

                                return@filter it.dateTimeMillis <= now
                            }.plus(
                                Goal.createReflectionsFromDate(
                                    Instant.now().toEpochMilli() + timePeriod.intervalMillis,
                                    timePeriod,
                                    goalCompletionDatePickerState.selectedDateMillis ?: 0
                                )
                            )

                        // Update the goal
                        setGoals(
                            context, goal.copy(
                                title = goalName,
                                description = goalDescription,
                                timePeriod = timePeriod,
                                completionDate =
                                goalCompletionDatePickerState.selectedDateMillis ?: 0,
                                id = goal.id,
                                reflections = reflections
                            )
                        )
                    }.invokeOnCompletion {
                        // Hide loading wheel and disable editing
                        isEditing = false
                        isSaving = false
                    }
                }

                // Bottom bar for saving/canceling editing
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(0.85f)
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        // Cancel button
                        Button(
                            onClick = {
                                isEditing = false

                                // Reset default values
                                goalName = goal.title
                                goalDescription = goal.description
                                selectedReflectionIntervalIndex = (
                                    GoalInterval.defaultList.indexOfFirst {
                                        goal.timePeriod.intervalMillis == it.intervalMillis
                                    }
                                )
                                goalCompletionDatePickerState.setSelection(goal.completionDate)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.cancel_editing))
                        }

                        // Save button
                        OutlinedButton(
                            onClick = saveButtonClick,
                            enabled = isGoalNameValid && isGoalDescriptionValid,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.save_changes))
                        }
                    }
                }
            }
        }
    }
}
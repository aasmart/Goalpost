package io.aasmart.goalpost.compose.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
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
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.LoadingWheel
import io.aasmart.goalpost.compose.components.OutlinedTextFieldDatePicker
import io.aasmart.goalpost.compose.components.OutlinedTextFieldDropdown
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.models.GoalInterval
import io.aasmart.goalpost.goals.models.GoalReflection
import io.aasmart.goalpost.utils.ColorUtils
import io.aasmart.goalpost.utils.GoalpostUtils
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

const val REMINDER_EDITING_GRACE_PERIOD_MILLIS = 2 * 60 * 60 * 1000L

/**
 * A component for the goal calendar that displays a goal reflection
 * as a day
 *
 * @param goalReflection The goal reflection
 * @param goalReflectionTimeMillis The time the user has to reflect on goals
 * @param day The day integer (1-31) to display in the day
 */
@Composable
private fun GoalReflectionCalendarDay(
    goalReflection: GoalReflection?,
    goalReflectionTimeMillis: Long,
    goalReflectionNav: (GoalReflection) -> Unit,
    day: Int
) {
    val context = LocalContext.current

    /*Displays different colors based on the reflection's completion status*/
    val buttonContainerColor = (
        if(goalReflection != null) {
            val goalReflectionInstant = Instant
                .ofEpochMilli(goalReflection.dateTimeMillis)
                .atZone(ZoneId.systemDefault())
                .with(ChronoField.MILLI_OF_DAY, goalReflectionTimeMillis)
                .toInstant()

            if(!goalReflection.isCompleted && Instant.now() > goalReflectionInstant)
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
    )

    Button(
        onClick = {
            if(goalReflection?.isCompleted == true)
                goalReflectionNav(goalReflection)
            else
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.need_completed_reflection_to_view),
                    Toast.LENGTH_SHORT
                ).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = buttonContainerColor),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        enabled = goalReflection != null,
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

/**
 * A component that displays a calendar where each day indicates any goal reflection
 * that is on that day
 *
 * @param goal The goal to display the reflections of
 * @param goalReflectionTimeMillis The time that the user performs goal reflections
 * @param goalReflectionNav Takes a goal and a reflection and navigates to the reflection page
 */
@Composable
private fun GoalReflectionCalendar(
    goal: Goal,
    goalReflectionTimeMillis: Long,
    goalReflectionNav: (Goal, GoalReflection) -> Unit,
) {
    // Creates a view at the current time, or about the completion time if it has been exceeded
    var zonedNow by rememberSaveable {
        mutableStateOf(
            Instant.ofEpochMilli(
                System.currentTimeMillis().coerceAtMost(goal.completionDate)
            ).atZone(ZoneId.systemDefault())
        )
    }
    val firstDayDateTime = zonedNow
        .withDayOfMonth(1)
        .with(ChronoField.MILLI_OF_DAY, 0)

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

        // Localized first letters of the days of the week
        val weekLetters = DateFormatSymbols(Locale.getDefault())
            .weekdays
            .filter { it.isNotEmpty() }
            .map { it[0].toString() }

        /*Maps a goal reflection's date time, which is changed to the beginning of the local day,
        * to the goal reflection itself*/
        val dateTimeGoalReflectionMap = goal.reflections
            .associateBy {
                Instant.ofEpochMilli(it.dateTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .with(ChronoField.MILLI_OF_DAY, 0)
                    .toInstant()
                    .toEpochMilli()
            }

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
            // Add empty days depending on when the first day of the month is
            items(firstDayOfWeek) {
                Box {}
            }
            items(month.maxLength()) {
                val dayMillis = firstDayDateTime
                    .plus(it.toLong(), ChronoUnit.DAYS)
                    .toInstant()
                    .toEpochMilli()

                val reflection = dateTimeGoalReflectionMap[dayMillis]
                GoalReflectionCalendarDay(
                    reflection,
                    day = (it + 1),
                    goalReflectionTimeMillis = goalReflectionTimeMillis,
                    goalReflectionNav = { goalReflection ->
                        goalReflectionNav(goal, goalReflection)
                    }
                )
            }
        }
    }
}

/**
 * A top app bar that contains the nav back arrow and the goal name
 *
 * @param goal The goal to display in the title
 * @param navBack A function to navigate to the previous screen
 */
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

/**
 * A floating action button that enabled editing
 *
 * @param onClicked A callback that should enable editing when clicked
 */
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

/**
 * A dialog that will prompt the user to confirm they want to delete a goal
 *
 * @param onVisibleChange A callback to change the dialog's visibility
 * @param deleteGoal A callback to delete the goal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmDeleteDialog(
    onVisibleChange: (Boolean) -> Unit,
    deleteGoal: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    AlertDialog(onDismissRequest = { onVisibleChange(false) }) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.confirm_delete_dialog),
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
                        coroutineScope.launch { deleteGoal() }
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

/**
 * A component that displays the details of a goal and also allows for the goal
 * to be edited
 *
 * @param goal The goal object
 * @param goalName A mutable field that contains the current goal name
 * @param isGoalNameValid A callback that returns the validity of a given name
 * @param updateGoalName A callback to modify the goal name
 * @param goalDescription A mutable field that contains the current goal description
 * @param isGoalDescriptionValid A function that returns the validity of a given description
 * @param updateGoalDescription A callback to modify the goal name
 * @param goalCompletionPickerState A state for the goal completion date picker
 * @param selectedReflectionFrequencyIndex The index of the selected goal reflection interval
 * @param updateSelectedReflectionFrequencyIndex A callback to update the selected goal reflection
 *        interval
 * @param isEditing True if the user is editing. If editing is enabled, all values can be changed
 * @param reflectionTimeMillis The time, in milliseconds that the user completes reflections
 * @param goalReflectionNav A function that navigates to a goal reflection's screen
 */
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
    isEditing: Boolean,
    reflectionTimeMillis: Long,
    goalReflectionNav: (Goal, GoalReflection) -> Unit,
    removeGoal: suspend () -> Unit,
) {
    val scroll = rememberScrollState()

    var confirmDeleteDialogVisible by remember {
        mutableStateOf(false)
    }

    if(confirmDeleteDialogVisible) {
        ConfirmDeleteDialog(
            onVisibleChange = {confirmDeleteDialogVisible = it},
            deleteGoal = removeGoal
        )
    }

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
                dateValidator = { goalCompleteDateTimeValidator(it, reflectionTimeMillis) }
            )
        } else {
            OutlinedTextFieldDatePicker(
                label = stringResource(id = R.string.goal_completion_date),
                currentTime = now,
                goalCompletionPickerState,
                expanded = false,
                onExpandedChange = {},
                dateValidator = { goalCompleteDateTimeValidator(it, reflectionTimeMillis) }
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
        GoalReflectionCalendar(
            goal = goal,
            goalReflectionTimeMillis = reflectionTimeMillis,
            goalReflectionNav = goalReflectionNav
        )

        if(isEditing) {
            OutlinedButton(
                onClick = {
                    confirmDeleteDialogVisible = true
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null
                )
                Text(text = stringResource(id = R.string.delete_goal))
            }
        }



        // !!! MUST BE LAST !!!
        if(isEditing)
            Spacer(modifier = Modifier.padding(bottom = 60.dp))
    }
}

/**
 * Creates a screen that allows a user to view information about a goal. The screen also allows
 * editing, if enabled
 *
 * @param goalpostNav The goalpost nav object
 * @param goalId The ID of the goal to view
 * @param reflectionTimeMillis The time of day, in milliseconds, that the user completes reflections
 * @param getGoals A function to retrieve all the set goals
 * @param setGoals A function to update a goal
 * @param goalReflectionNav A function to navigate to a goal reflection screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(
    goalpostNav: GoalpostNav,
    goalId: String,
    reflectionTimeMillis: Long,
    getGoals: (Context) -> Flow<List<Goal>>,
    setGoals: suspend (Context, Goal) -> Unit,
    removeGoal: suspend (Context, goalId: String) -> Unit,
    goalReflectionNav: (Goal, GoalReflection) -> Unit
) {
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

    val editingEnabledUntilMillis = goal
        ?.beginDate
        ?.plus(REMINDER_EDITING_GRACE_PERIOD_MILLIS)
        ?: 0
    fun checkEditingEnabled(): Boolean {
        return System.currentTimeMillis() < editingEnabledUntilMillis
                && goal?.isCompleted() == false
    }

    Scaffold(
        topBar = { DetailsTopAppBar(goal = goal, navBack = goalpostNav.up) },
        floatingActionButton = {
            if(!isEditing && checkEditingEnabled())
                EditFloatingActionButton {
                    if(!checkEditingEnabled()) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.editing_is_disabled),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        isEditing = true
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
                Text(text = stringResource(R.string.failed_goal_load))
                Button(onClick = goalpostNav.goalManager) {
                    Text(text = stringResource(id = R.string.return_to_goal_manager))
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
                isEditing = isEditing,
                reflectionTimeMillis = reflectionTimeMillis,
                goalReflectionNav = goalReflectionNav,
                removeGoal = {
                    removeGoal(context, goalId)
                    goalpostNav.up()
                }
            )

            if(isEditing) {
                val saveButtonClick: () -> Unit = {
                    coroutineScope.launch {
                        // Show loading wheel
                        isSaving = true

                        val timePeriod = GoalInterval.defaultList[
                            selectedReflectionIntervalIndex
                        ]

                        // Set completion time to start of day to remove time zone issues
                        val completionDateTime = goalCompletionDatePickerState
                            .selectedDateMillis
                            ?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.of("UTC"))
                                    .with(ChronoField.MILLI_OF_DAY, GoalpostUtils.DAY_MS - 1)
                                    .toInstant()
                            }

                        /*
                        * Regenerates the reflections based on the new parameters.
                        * Preserves older reflections (including the one of the current day).
                        * This is why Goal#createReflectionFromDate has an additional time
                        * period added to it
                        */
                        val zonedNow = ZonedDateTime
                            .ofInstant(
                                Instant.now(),
                                ZoneId.systemDefault()
                            ).with(ChronoField.MILLI_OF_DAY, GoalpostUtils.DAY_MS - 1)
                        val reflections = goal.reflections
                            .filter {
                                val zonedReflectionDateTime = Instant
                                    .ofEpochMilli(it.dateTimeMillis)
                                    .atZone(ZoneId.systemDefault())

                                return@filter zonedReflectionDateTime <= zonedNow || it.isCompleted
                            }.plus(
                                Goal.createReflectionsFromDate(
                                    System.currentTimeMillis() + GoalpostUtils.DAY_MS,
                                    timePeriod,
                                    completionDateTime?.toEpochMilli() ?: 0
                                )
                            )

                        // Update the goal
                        setGoals(
                            context,
                            goal.copy(
                                title = goalName,
                                description = goalDescription,
                                timePeriod = timePeriod,
                                completionDate = completionDateTime?.toEpochMilli() ?: 0,
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
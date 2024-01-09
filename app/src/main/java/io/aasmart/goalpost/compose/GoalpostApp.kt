package io.aasmart.goalpost.compose

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.screens.CreateGoalScreen
import io.aasmart.goalpost.compose.screens.GoalCalendarScreen
import io.aasmart.goalpost.compose.screens.GoalDetailsScreen
import io.aasmart.goalpost.compose.screens.GoalReflectionScreen
import io.aasmart.goalpost.compose.screens.GoalsManager
import io.aasmart.goalpost.compose.screens.GoalsReflectionScreen
import io.aasmart.goalpost.compose.screens.HomeScreen
import io.aasmart.goalpost.compose.screens.Screen
import io.aasmart.goalpost.compose.screens.settings.SettingsCategoryScreen
import io.aasmart.goalpost.compose.screens.settings.SettingsScreen
import io.aasmart.goalpost.compose.viewmodels.GoalpostViewModel
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.models.Goal
import io.aasmart.goalpost.goals.scheduleReflectionAlarm
import io.aasmart.goalpost.goals.scheduleRemindersAlarm
import io.aasmart.goalpost.utils.GoalpostUtils
import io.aasmart.goalpost.utils.InputUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun PreferredNamePrompt() {
    val minNameLength = 1
    val maxNameLength = 32

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var preferredName by rememberSaveable {
        mutableStateOf("")
    }
    val isNameValid = InputUtils.isValidLength(
        preferredName.trim(), 
        minNameLength, 
        maxNameLength
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.like_to_call_you)
        )
        OutlinedTextField(
            value = preferredName,
            onValueChange = { preferredName = it },
            isError = !isNameValid,
            label = { Text(text = stringResource(id = R.string.name)) },
            supportingText = {
                if(!isNameValid)
                    Text(
                        text = stringResource(id = R.string.between_num_characters)
                            .replace(
                                "{LABEL}",
                                stringResource(id = R.string.name)
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
                                "${preferredName.trim().length}/${Goal.NAME_MAX_LENGTH}"
                            )
                    )
            },
            modifier = Modifier.fillMaxWidth(.95f)
        )

        val updateName = suspend {
             context.settingsDataStore.updateData {
                 it.toBuilder().setPreferredName(preferredName).build()
             }
        }

        OutlinedButton(
            onClick = {
                coroutineScope.launch { updateName() }
            },
            enabled = isNameValid,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = "${stringResource(id = R.string.submit)} ${stringResource(id = R.string.name)}"
            )
        }
    }
}

@Composable
fun BottomNavBar(
    homeHandle: () -> Unit,
    goalManagerHandle: () -> Unit,
    settingsHandle: () -> Unit,
    goalCalendarHandle: () -> Unit,
    createGoalHandle: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home button
            IconButton(
                onClick = homeHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.9f),
            ) {
                Icon(
                    Icons.Filled.Home,
                    stringResource(id = R.string.home),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Goal manager button
            IconButton(
                onClick = goalManagerHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.8f)
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    stringResource(id = R.string.goal_manager),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Create goal button
            FilledIconButton(
                onClick = createGoalHandle,
                modifier = Modifier
                    .weight(.8f)
                    .aspectRatio(1f)
            ) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(id = R.string.create_goal),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // View goal schedule button
            IconButton(
                onClick = goalCalendarHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.8f)
            ) {
                Icon(
                    Icons.Filled.DateRange,
                    stringResource(id = R.string.goal_calendar),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Settings button
            IconButton(
                onClick = settingsHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.8f)
            ) {
                Icon(
                    Icons.Filled.Settings,
                    stringResource(id = R.string.settings),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun GoalReflectionDialog(
    reflectionsNav: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.9f)

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.time_to_reflect),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(id = R.string.reflection_dialog))

                TextButton(
                    onClick = reflectionsNav,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(id = R.string.reflections_dialog_button))
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = stringResource(id = R.string.reflections_dialog_button)
                    )
                }
            }
        }
    }
}

@Composable
fun GoalpostNavScaffold(
    nav: GoalpostNav,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                homeHandle = nav.home,
                goalManagerHandle = nav.goalManager,
                settingsHandle = nav.settings,
                goalCalendarHandle = nav.goalCalendar,
                createGoalHandle = nav.createGoal
            )
        }
    ) {
        content(it)
    }
}

@Composable
fun LoadingWheel() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun GoalpostApp(
    navController: NavHostController,
    appViewModel: GoalpostViewModel = viewModel()
) {
    CheckPermissions()

    val context = LocalContext.current

    val goals by appViewModel
        .getGoals(context)
        .collectAsStateWithLifecycle(initialValue = null)

    val goalpostNav = GoalpostNav(
        home = { navController.navigate(Screen.Home.route) },
        settings = { navController.navigate(Screen.Settings.route) },
        settingCategory = { navController.navigate(Screen.Settings.Category.createRoute(it)) },
        goalManager = { navController.navigate(Screen.GoalManager.route) },
        createGoal = { navController.navigate(Screen.CreateGoal.route) },
        goalCalendar = { navController.navigate(Screen.GoalCalendar.route) },
        up = { navController.navigateUp() }
    )

    // Reflection dialog stuff
    val settings by context.settingsDataStore.data.collectAsStateWithLifecycle(initialValue = null)
    val showReflectionDialog = settings?.needsToReflect == true
    val navGoalReflection = { navController.navigate(Screen.GoalReflections.route) }

    if(settings == null || goals == null) {
        LoadingWheel()
        return
    }

    if(settings?.preferredName?.isEmpty() == true) {
        PreferredNamePrompt()
        return
    }

    LaunchedEffect(
        settings?.morningReminderTimeMs,
        settings?.midDayReminderTimeMs,
        settings?.eveningReminderTimeMs
    ) {
        scheduleRemindersAlarm(context)
    }

    LaunchedEffect(settings?.goalReflectionTimeMs) {
        scheduleReflectionAlarm(context)
    }

    /*If the user is in the app, try to schedule the reflection time to reduce
    * the reliance on an alarm*/
    LaunchedEffect(settings?.goalReflectionTimeMs) {
        if(settings?.goalReflectionTimeMs == null)
            return@LaunchedEffect

        val reflectionDateTime = settings?.let {
            GoalpostUtils.timeAsTodayDateTime(it.goalReflectionTimeMs)
        } ?: Instant.now()

        delay(reflectionDateTime.toEpochMilli() - System.currentTimeMillis())

        val incompleteGoals = goals?.filter {
            it.getCurrentReflection(Instant.now())?.isCompleted == false
        } ?: emptyList()

        if(incompleteGoals.isNotEmpty()) {
            context.settingsDataStore.updateData {
                it.toBuilder().setNeedsToReflect(true).build()
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            if(showReflectionDialog)
                GoalReflectionDialog { navGoalReflection() }

            HomeScreen(
                goalpostNav,
                goals = goals?.toTypedArray() ?: emptyArray(),
                preferredName = settings?.preferredName ?: ""
            )
        }
        composable(Screen.GoalManager.route, Screen.GoalManager.args) {
            if(showReflectionDialog)
                GoalReflectionDialog { navGoalReflection() }

            GoalsManager(
                goalpostNav,
                goals = goals ?: emptyList(),
                manageGoalNav = {
                    navController.navigate(
                        Screen.GoalDetails.createRoute(it.id)
                    )
                }
            )
        }
        composable(Screen.CreateGoal.route) {
            if(showReflectionDialog)
                GoalReflectionDialog { navGoalReflection() }

            CreateGoalScreen(
                goalpostNav = goalpostNav,
                addGoal = { goal: Goal -> appViewModel.addGoal(context = context, goal) },
                reflectionTime = settings?.goalReflectionTimeMs ?: 0
            )
        }
        composable(Screen.GoalCalendar.route) {
            if(showReflectionDialog)
                GoalReflectionDialog { navGoalReflection() }

            GoalCalendarScreen(
                goalpostNav
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                goalpostNav
            )
        }
        composable(Screen.Settings.Category.route, Screen.Settings.Category.args) {
            SettingsCategoryScreen(
                goalpostNav = goalpostNav,
                categoryId = it.arguments?.getString("categoryId") ?: ""
            )
        }
        composable(Screen.GoalDetails.route, Screen.GoalDetails.args) {
            GoalDetailsScreen(
                goalpostNav = goalpostNav,
                goalId = it.arguments?.getString("goalId") ?: "",
                reflectionTimeMillis = settings?.goalReflectionTimeMs ?: 0,
                getGoals = { context -> appViewModel.getGoals(context) },
                setGoals = { context, goal -> appViewModel.setGoal(context, goal) },
                removeGoal = { context, goalId -> appViewModel.removeGoal(context, goalId) },
                goalReflectionNav = { goal, reflection ->
                    navController.navigate(
                        Screen.GoalReflections.Goal.createRoute(
                            goal.id,
                            reflection.id
                        )
                    )
                }
            )
        }
        composable(Screen.GoalReflections.route) {
            GoalsReflectionScreen(
                goals = goals ?: emptyList(),
                navGoalReflection = {
                    navController.navigate(
                        Screen.GoalReflections.Goal.createRoute(
                            it.id,
                            it.getCurrentReflection(Instant.now())?.id ?: ""
                        )
                    )
                },
                backNav = { navController.navigateUp() },
                homeNav = goalpostNav.home
            )
        }
        composable(Screen.GoalReflections.Goal.route, Screen.GoalReflections.Goal.args) {
            GoalReflectionScreen(
                goalId = it.arguments?.getString("goalId") ?: "",
                getGoals = { context -> appViewModel.getGoals(context) },
                setGoal = { context, goal -> appViewModel.setGoal(context, goal) },
                { navController.navigateUp() }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions() {
    val intentFullscreenPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberPermissionState(permission = Manifest.permission.USE_FULL_SCREEN_INTENT)
    } else {
        null
    }

    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    else
        null

    LaunchedEffect(key1 = intentFullscreenPermission) {
        if (intentFullscreenPermission != null && !intentFullscreenPermission.status.isGranted)
            intentFullscreenPermission.launchPermissionRequest()

        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted)
            postNotificationPermission.launchPermissionRequest()
    }
}
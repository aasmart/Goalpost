package io.aasmart.goalpost.compose

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import io.aasmart.goalpost.compose.screens.GoalsManager
import io.aasmart.goalpost.compose.screens.HomeScreen
import io.aasmart.goalpost.compose.screens.Screen
import io.aasmart.goalpost.compose.screens.SettingsScreen
import io.aasmart.goalpost.compose.viewmodels.GoalpostViewModel
import io.aasmart.goalpost.goals.models.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    )
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
        containerColor = MaterialTheme.colorScheme.primary,
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
                Icon(Icons.Filled.Home, "Home", modifier = Modifier.fillMaxSize())
            }

            // Goal manager button
            IconButton(
                onClick = goalManagerHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.8f)
            ) {
                Icon(Icons.Filled.CheckCircle, "Goal Manager", modifier = Modifier.fillMaxSize())
            }

            // Create goal button
            FilledIconButton(
                onClick = createGoalHandle,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0, 177, 224, 200)
                ),
                modifier = Modifier
                    .shadow(4.dp, shape = CircleShape, ambientColor = Color(0, 177, 224))
                    .weight(.8f)
                    .aspectRatio(1f)
            ) {
                Icon(Icons.Filled.Add, "Create Goal", modifier = Modifier.fillMaxSize())
            }

            // View goal schedule button
            IconButton(
                onClick = goalCalendarHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.8f)
            ) {
                Icon(Icons.Filled.DateRange, "Goal Schedule", modifier = Modifier.fillMaxSize())
            }

            // Settings button
            IconButton(
                onClick = settingsHandle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(.8f)
            ) {
                Icon(Icons.Filled.Settings, "Settings", modifier = Modifier.fillMaxSize())
            }
        }
    }
}


@Composable
fun GoalpostApp(
    navController: NavHostController,
    appViewModel: GoalpostViewModel = viewModel()
) {
    CheckPermissions()

    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val homeHandle = { navController.navigate(Screen.Home.route) }
    val settingsHandle = { navController.navigate(Screen.Settings.route) }
    val goalManagerHandle = { navController.navigate(Screen.GoalManager.route) }
    val createGoalHandle = { navController.navigate(Screen.CreateGoal.route) }
    val goalCalendarHandle = { navController.navigate(Screen.GoalCalendar.route) }

    val goals = appViewModel.getGoals(context).collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            BottomNavBar(
                homeHandle = homeHandle,
                goalManagerHandle = goalManagerHandle,
                settingsHandle = settingsHandle,
                goalCalendarHandle = goalCalendarHandle,
                createGoalHandle = createGoalHandle
            )
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                HomeScreen(
                    padding,
                    goalManagerHandle = goalManagerHandle,
                    createGoalHandle = createGoalHandle,
                    goals = goals.value.toTypedArray()
                )
            }
            composable(Screen.GoalManager.route, Screen.GoalManager.args) {
                GoalsManager(
                    padding,
                    createGoalHandle = createGoalHandle,
                    goals = goals.value,
                    calendarScreenNav = goalCalendarHandle,
                    manageGoalNav = {
                        navController.navigate(
                            Screen.GoalDetails.createRoute(it.id)
                        )
                    }
                )
            }
            composable(Screen.CreateGoal.route) {
                CreateGoalScreen(
                    padding,
                    { goal: Goal -> appViewModel.addGoal(context = context, goal) },
                    goalManagerHandle = goalManagerHandle
                )
            }
            composable(Screen.GoalCalendar.route) {
                GoalCalendarScreen(scaffoldPadding = padding)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    padding
                )
            }
            composable(Screen.GoalDetails.route, Screen.GoalDetails.args) {
                GoalDetailsScreen(
                    scaffoldPadding = padding,
                    goalId = it.arguments?.getString("goalId") ?: "",
                    getGoals = { context -> appViewModel.getGoals(context) },
                    goalManagerNav = goalManagerHandle
                )
            }
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
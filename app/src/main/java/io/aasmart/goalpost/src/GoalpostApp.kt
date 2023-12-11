package io.aasmart.goalpost.src

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.aasmart.goalpost.R
import io.aasmart.goalpost.src.goals.Screen
import io.aasmart.goalpost.src.goals.models.Goal

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
private fun BottomBar(
    homeHandle: () -> Unit,
    goalManagerHandle: () -> Unit,
    settingsHandle: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = homeHandle
            ) {
                Icon(Icons.Filled.Home, "Home", modifier = Modifier.fillMaxSize())
            }

            FilledIconButton(
                onClick = goalManagerHandle,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.apply { Color(this.red, this.green, this.blue, 0.5f) }
                ),
                modifier = Modifier
                    .shadow(4.dp, shape = CircleShape)
            ) {
                Icon(Icons.Filled.Add, "Settings", modifier = Modifier.fillMaxSize())
            }

            IconButton(
                onClick = settingsHandle
            ) {
                Icon(Icons.Filled.Settings, "Settings", modifier = Modifier.fillMaxSize())
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
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
    val goalManagerHandle = { navController.navigate(Screen.GoalManager.route )}

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable("home") {
            Scaffold(
                topBar = { /*TopBar()*/ },
                bottomBar = {
                    BottomBar(
                        homeHandle = homeHandle,
                        settingsHandle = settingsHandle,
                        goalManagerHandle = goalManagerHandle
                    )
                }
            ) {
                Column(modifier = Modifier.padding(it)) {
                    Greeting()
                    GoalsSnippetCard(
                        emptyArray(),
                        2
                    ) { navController.navigate(Screen.GoalManager.route) }
                }
            }
        }
        composable(Screen.GoalManager.route) {
            GoalsManager(
                homeHandle = homeHandle,
                settingsHandle = settingsHandle,
                goalManagerHandle = goalManagerHandle,
                goals = emptyList()
            )
        }
        composable(Screen.Settings.route) {
            Settings(homeHandle, goalManagerHandle, settingsHandle)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    homeHandle: () -> Unit,
    goalManagerHandle: () -> Unit,
    settingsHandle: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                homeHandle = homeHandle,
                goalManagerHandle = goalManagerHandle,
                settingsHandle = settingsHandle
            )
        }
    ) {
        it
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsManager(
    homeHandle: () -> Unit,
    goalManagerHandle: () -> Unit,
    settingsHandle: () -> Unit,
    goals: List<Goal>
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                homeHandle = homeHandle,
                settingsHandle = settingsHandle,
                goalManagerHandle = goalManagerHandle
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {

        }
    }
}

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
    goals: Array<Goal> = emptyArray(),
    displayNumGoals: Int = 2,
    interactNavigate: () -> Unit
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
                Text("It looks like you haven't set any goals. Consider " +
                        "pressing \"${stringResource(id = R.string.set_goals)} to begin " +
                        "setting goals.")

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.small,
                    onClick = { interactNavigate() }
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
                        onClick = { interactNavigate() }
                    ) {
                        Text(text = stringResource(R.string.view_goals))
                    }
                }
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
        if(intentFullscreenPermission != null && !intentFullscreenPermission.status.isGranted)
            intentFullscreenPermission.launchPermissionRequest()

        if(postNotificationPermission != null && !postNotificationPermission.status.isGranted)
            postNotificationPermission.launchPermissionRequest()
    }
}

@Composable
@Preview
fun GoalsSnippetCardPreview() {
    GoalsSnippetCard(interactNavigate = {})
}
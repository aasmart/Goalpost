package io.aasmart.goalpost.src

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import io.aasmart.goalpost.R
import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.isGranted
import io.aasmart.goalpost.src.goals.notifications.SetGoalsNotification

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
private fun BottomBar() {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalpostApp(appViewModel: GoalpostViewModel = viewModel()) {
    CheckPermissions()

    val context = LocalContext.current

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Button(onClick = {
                SetGoalsNotification.pushNotification(context)
            }) {
                Text("Notification")
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
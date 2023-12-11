package io.aasmart.goalpost

import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import io.aasmart.goalpost.src.GoalpostApp
import io.aasmart.goalpost.src.goals.notifications.createNotificationChannel
import io.aasmart.goalpost.ui.theme.GoalpostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            val flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                .or(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                .or(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
                .or(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            window.addFlags(flags)
        }

        setContent {
            val navController = rememberNavController()

            GoalpostTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GoalpostApp(navController)
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoalpostTheme {
        Greeting("Android")
    }
}*/

package io.aasmart.goalpost.compose.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold

@Composable
fun GoalCalendarScreen(
    goalpostNav: GoalpostNav
) {
    GoalpostNavScaffold(nav = goalpostNav) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Text(text = "Coming soon...")
        }
    }
}
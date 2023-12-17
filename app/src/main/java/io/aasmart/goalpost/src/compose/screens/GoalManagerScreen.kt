package io.aasmart.goalpost.src.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import io.aasmart.goalpost.src.goals.models.Goal

@Composable
fun GoalsManager(
    scaffoldPadding: PaddingValues,
    createGoalHandle: () -> Unit,
    goals: List<Goal>
) {
    if(goals.isEmpty()) {
        Column(
            modifier = Modifier.padding(scaffoldPadding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You don't have any goals set.")
            Button(onClick = createGoalHandle) {
                Text("Set Goals")
            }
        }
    } else {
        Column(modifier = Modifier.padding(scaffoldPadding).fillMaxSize()) {
            Text("test")
        }
    }
}
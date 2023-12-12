package io.aasmart.goalpost.src.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.aasmart.goalpost.src.compose.BottomNavBar
import io.aasmart.goalpost.src.goals.models.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsManager(
    scaffoldPadding: PaddingValues,
    goals: List<Goal>
) {
    if(goals.isEmpty()) {
        Column(
            modifier = Modifier.padding(scaffoldPadding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You don't have any goals set.")
            Button(onClick = { /*TODO*/ }) {
                Text("Set Goals")
            }
        }
    } else {
        Column(modifier = Modifier.padding(scaffoldPadding)) {
            Text("test")
        }
    }
}

@Composable
fun CreateGoalDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {

    }
}
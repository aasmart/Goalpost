package io.aasmart.goalpost.compose.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.goals.models.Goal
import kotlinx.coroutines.flow.Flow

@Composable
fun GoalDetailsScreen(
    goalpostNav: GoalpostNav,
    goalId: String,
    getGoals: (Context) -> Flow<List<Goal>>,
) {
    Scaffold(

    ) { padding ->
        val goals = getGoals(LocalContext.current).collectAsState(initial = null).value
        val goal = goals?.find { goal -> goal.id == goalId }
        if(goal == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Could not find goal")
                Button(onClick = goalpostNav.goalManager) {
                    Text(text = "Return to Goal Manager")
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Text(text = goal.title)

        }
    }
}
package io.aasmart.goalpost.src.compose

import android.content.Context
import androidx.lifecycle.ViewModel
import io.aasmart.goalpost.src.goals.GoalStorage
import io.aasmart.goalpost.src.goals.models.Goal
import kotlinx.coroutines.flow.Flow

class GoalpostViewModel : ViewModel() {
    fun getGoals(context: Context) : Flow<List<Goal>> {
        return GoalStorage.getInstance(context)
            .getGoals()
    }

    suspend fun addGoal(context: Context, goal: Goal) {
        GoalStorage.getInstance(context).addGoal(goal)
    }
}
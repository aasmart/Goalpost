package io.aasmart.goalpost.compose.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import io.aasmart.goalpost.data.GoalStorage
import io.aasmart.goalpost.goals.models.Goal
import kotlinx.coroutines.flow.Flow

class GoalpostViewModel : ViewModel() {
    fun getGoals(context: Context) : Flow<List<Goal>> {
        return GoalStorage.getInstance(context)
            .getGoals()
    }

    suspend fun addGoal(context: Context, goal: Goal) {
        GoalStorage.getInstance(context).addGoal(goal)
    }

    suspend fun setGoal(context: Context, goal: Goal) {
        GoalStorage.getInstance(context).setGoal(goal)
    }

    suspend fun removeGoal(context: Context, goalId: String) {
        GoalStorage.getInstance(context).removeGoal(goalId)
    }
}
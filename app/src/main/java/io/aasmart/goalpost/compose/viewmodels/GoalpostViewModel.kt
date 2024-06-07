package io.aasmart.goalpost.compose.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.aasmart.goalpost.api.AuthResult
import io.aasmart.goalpost.api.GoalpostRepository
import io.aasmart.goalpost.compose.state.GoalpostState
import io.aasmart.goalpost.data.GoalStorage
import io.aasmart.goalpost.goals.models.Goal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalpostViewModel @Inject constructor(
    private val repository: GoalpostRepository,
    private val prefs: SharedPreferences
) : ViewModel() {
    var state by mutableStateOf(GoalpostState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    fun getFirstName(): String {
        if(!prefs.contains("first_name"))
            return ""
        return prefs.getString("first_name", "") ?: ""
    }

    fun getUser() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        val result = repository.user()
        //state = state.copy(isLoading = false, retrievedUserName = true)
        resultChannel.send(result)
    }

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
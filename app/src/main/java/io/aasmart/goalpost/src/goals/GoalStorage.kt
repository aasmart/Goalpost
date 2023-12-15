package io.aasmart.goalpost.src.goals

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.aasmart.goalpost.src.goals.models.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GoalStorage(private val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: GoalStorage? = null

        private val Context.dataStore by preferencesDataStore(name = "goal_saves")
        val GOAL_SAVES = stringPreferencesKey("goal_saves")

        @Synchronized
        fun getInstance(context: Context): GoalStorage {
            return Instance ?: run {
                val instance = GoalStorage(context.applicationContext)
                Instance = instance
                instance
            }
        }

        private val goalJson = Json {
            allowStructuredMapKeys = true
        }
    }

    fun getGoals() : Flow<List<Goal>> {
        return context.dataStore.data
            .map { preferences ->
                preferences[GOAL_SAVES]?.let { json ->
                    goalJson.decodeFromString(json)
                } ?: emptyList()
            }
    }

    suspend fun addGoal(goal: Goal) {
        context.dataStore.edit { preferences ->
            val goals = preferences[GOAL_SAVES]?.let { json ->
                goalJson.decodeFromString<List<Goal>>(json)
            }?.toMutableList() ?: mutableListOf()

            goals.add(goal)
            preferences[GOAL_SAVES] = goalJson.encodeToString(goals)
        }
    }

    suspend fun removeGoal(goalId: String) {
        context.dataStore.edit { preferences ->
            val goals = preferences[GOAL_SAVES]?.let { json ->
                goalJson.decodeFromString<List<Goal>>(json)
            }?.toMutableList() ?: return@edit

            goals.removeAll { goal -> goal.id == goalId }
            preferences[GOAL_SAVES] = goalJson.encodeToString(goals)
        }
    }

    suspend fun setGoal(goal: Goal) {
        context.dataStore.edit { preferences ->
            val goals = preferences[GOAL_SAVES]?.let { json ->
                goalJson.decodeFromString<List<Goal>>(json)
            }?.toMutableList() ?: return@edit

            goals.replaceAll {
                if(it.id == goal.id)
                    goal
                else
                    it
            }
            preferences[GOAL_SAVES] = goalJson.encodeToString(goals)
        }
    }

    suspend fun clearGoals() {
        context.dataStore.edit { it.remove(GOAL_SAVES) }
    }
}
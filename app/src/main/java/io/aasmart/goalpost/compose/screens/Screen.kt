package io.aasmart.goalpost.compose.screens

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {
    object Home : Screen("home")
    object GoalManager : Screen("goalManager")
    object Settings : Screen("settings")
    object GoalCalendar : Screen("goalCalendar")
    object CreateGoal : Screen("createGoal")

    object GoalDetails : Screen(
        "goalDetails/{goalId}",
        args = listOf(navArgument("goalId") {
            type = NavType.StringType
        })
    ) {
        fun createRoute(goalId: String) = "goalDetails/$goalId"
    }

    object GoalReflections: Screen(
        "goalReflections"
    ) {
        object Goal : Screen(
            "${GoalReflections.route}/{goalId}",
            args = listOf(navArgument("goalId") {
                type = NavType.StringType
            })
        ) {
            fun createRoute(goalId: String) = "${GoalReflections.route}/$goalId"
        }
    }
}
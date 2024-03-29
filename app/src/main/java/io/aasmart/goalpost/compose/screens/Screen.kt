package io.aasmart.goalpost.compose.screens

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {
    object Config {
        val slideOutRoute = listOf(
            GoalDetails.route,
            Settings.Category.route,
            GoalReflections.Goal.route
        )
    }

    object Home : Screen("home")
    object GoalManager : Screen("goalManager")
    object Settings : Screen("settings") {
        object Category : Screen(
            "${Settings.route}/{categoryId}",
            args = listOf(navArgument("categoryId") {
                type = NavType.StringType
            })
        ) {
            fun createRoute(categoryId: String) = "${Settings.route}/${categoryId}"
        }
    }
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
            "${GoalReflections.route}/{goalId}/{reflectionId}",
            args = listOf(
                navArgument("goalId") { type = NavType.StringType },
                navArgument("reflectionId") { type = NavType.StringType }
            )
        ) {
            fun createRoute(
                goalId: String,
                reflectionId: String
            ) = "${GoalReflections.route}/$goalId/$reflectionId"
        }
    }
}
package io.aasmart.goalpost.src.goals

sealed class Screen(
    val route: String
) {
    object Home : Screen("home")
    object GoalManager : Screen("goalManager")
    object Settings : Screen("settings")
}
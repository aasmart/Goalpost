package io.aasmart.goalpost.src.compose.screens

sealed class Screen(
    val route: String
) {
    object Home : Screen("home")
    object GoalManager : Screen("goalManager")
    object Settings : Screen("settings")
    object GoalCalendar : Screen("goalCalendar")
    object CreateGoal : Screen("createGoal")
}
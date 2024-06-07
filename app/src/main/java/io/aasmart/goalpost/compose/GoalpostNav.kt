package io.aasmart.goalpost.compose

data class GoalpostNav(
    val home: () -> Unit,
    val goalManager: () -> Unit,
    val settings: () -> Unit,
    val settingCategory: (String) -> Unit,
    val goalCalendar: () -> Unit,
    val createGoal: () -> Unit,
    val login: () -> Unit,
    val signup: () -> Unit,
    val up: () -> Unit,
)

package io.aasmart.goalpost.compose

data class GoalpostNav(
    val home: () -> Unit,
    val goalManager: () -> Unit,
    val settings: () -> Unit,
    val goalCalendar: () -> Unit,
    val createGoal: () -> Unit,
)

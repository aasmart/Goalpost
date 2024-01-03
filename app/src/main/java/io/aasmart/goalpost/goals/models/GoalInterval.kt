package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GoalInterval(
    val name: String,
    val intervalMillis: Long,
    val id: String = UUID.randomUUID().toString()
) {
    companion object Defaults {
        val daily = GoalInterval("Daily", 86_400_000)
        val everyOtherDay = GoalInterval("Every Other Day", 172800000)
        val weekly = GoalInterval("Weekly", 604_800_000)
        val bimonthly = GoalInterval("Bi-Monthly", 1_209_600_000)
        val monthly = GoalInterval("Monthly", 2_419_200_000)
        val biyearly = GoalInterval("Bi-Yearly", 14_515_200_000)
        val yearly = GoalInterval("Yearly", 29_030_400_000)

        val defaultList = listOf(
            daily,
            everyOtherDay,
            weekly,
            bimonthly,
            monthly,
            biyearly,
            yearly
        )
    }
}

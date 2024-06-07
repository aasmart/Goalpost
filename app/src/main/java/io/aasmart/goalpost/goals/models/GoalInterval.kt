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
        private val daily = GoalInterval("Daily", 86_400_000)
        private val everyOtherDay = GoalInterval("Every Other Day", 172800000)
        private val weekly = GoalInterval("Weekly", 604_800_000)
        private val bimonthly = GoalInterval("Bi-Monthly", 1_209_600_000)
        private val monthly = GoalInterval("Monthly", 2_419_200_000)
        private val biyearly = GoalInterval("Bi-Yearly", 14_515_200_000)
        private val yearly = GoalInterval("Yearly", 29_030_400_000)

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

package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable

@Serializable
data class GoalReflection(
    val isCompleted: Boolean = false,
    val dateTimeMillis: Long,
    val madeProgress: Float? = null,
    val madeProgressReflection: String? = null,
    val couldDoBetter: Float? = null,
    val couldDoBetterReflection: String? = null,
    val stepsToImprove: String? = null,
) {
    companion object {
        /** b
        * The minimum value for the float-based responses (e.g. 'Made Progress')
        */
        const val SLIDER_MIN_VAL = 0f

        /**
        * The maximum value for the float-based responses (e.g. 'Made Progress')
        */
        const val SLIDER_MAX_VAL = 4f
    }
}

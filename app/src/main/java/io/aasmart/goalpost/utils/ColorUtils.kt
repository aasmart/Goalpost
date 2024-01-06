package io.aasmart.goalpost.utils

import androidx.compose.ui.graphics.Color
import androidx.core.math.MathUtils

object ColorUtils {
    private fun lerp(start: Float, stop: Float, amount: Float) =
        start + amount * (stop - start)
    fun lerp(start: Color, stop: Color, amount: Float): Color {
       return Color(
           red = lerp(start.red, stop.red, amount),
           green = lerp(start.green, stop.green, amount),
           blue = lerp(start.blue, stop.blue, amount),
           alpha = lerp(start.alpha, stop.alpha, amount)
       )
    }
}
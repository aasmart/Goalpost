package io.aasmart.goalpost.compose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LABEL_OFFSET = (-12).dp

@Composable
private fun DrawStepLabels(
    stepLabels: Map<Int, String> = emptyMap(),
    steps: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth().offset(y = LABEL_OFFSET)
    ) {
        for(i in 0..(steps + 1)) {
            if(stepLabels[i] == null)
                Text(text = "", modifier = Modifier.weight(1f))
            stepLabels[i]?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GoalpostSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit = {},
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors(),
    stepLabels: Map<Int, String> = emptyMap()
) {
    Column(modifier = modifier) {
        label()
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                valueRange = valueRange,
                steps = steps,
                onValueChangeFinished = onValueChangeFinished,
                interactionSource = interactionSource,
                colors = colors,
                modifier = Modifier.fillMaxWidth()
            )
            DrawStepLabels(
                stepLabels = stepLabels,
                steps = steps
            )
        }
    }
}
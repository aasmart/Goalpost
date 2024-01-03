package io.aasmart.goalpost.compose.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class SettingCategory(
    val categoryName: String,
    val categoryId: String,
    val categoryIcon: ImageVector,
    val content: @Composable () -> Unit
) {
    Goals(
        "Goals",
        "goals",
        Icons.Filled.CheckCircle,
        content = { GoalCategoryContent() }
    )
}
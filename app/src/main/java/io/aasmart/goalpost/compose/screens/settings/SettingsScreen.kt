package io.aasmart.goalpost.compose.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.GoalpostNavScaffold

@Composable
private fun CategoryItem(
    goalpostSetting: SettingCategory,
    height: Dp,
    navSettingsCategory: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .clickable {
                navSettingsCategory(goalpostSetting.categoryId)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            goalpostSetting.categoryIcon,
            contentDescription = goalpostSetting.categoryName,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .aspectRatio(1f)
                .weight(.2f)
        )
        Text(
            text = goalpostSetting.categoryName,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(2f)
        )
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .aspectRatio(1f)
                .weight(.25f)
        )
    }
}

@Composable
fun SettingsScreen(
    goalpostNav: GoalpostNav
) {
    val categories = SettingCategory.values()
    GoalpostNavScaffold(nav = goalpostNav) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(categories) {
                CategoryItem(
                    goalpostSetting = it,
                    height = 50.dp,
                    navSettingsCategory = goalpostNav.settingCategory
                )
            }
        }
    }
}
package io.aasmart.goalpost.compose.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.R
import io.aasmart.goalpost.compose.GoalpostNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryTopAppBar(
    category: SettingCategory,
    navBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = category.categoryName)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            IconButton(onClick = navBack) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.go_back),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}

@Composable
fun SettingsCategoryScreen(
    goalpostNav: GoalpostNav,
    categoryId: String,
) {
    val category = SettingCategory.values().first { it.categoryId == categoryId }
    Scaffold(
        topBar = {
            CategoryTopAppBar(
                category = category,
                navBack = goalpostNav.up)
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(8.dp).fillMaxSize()
        ) {
            category.content()
        }
    }
}
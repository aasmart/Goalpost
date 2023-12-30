package io.aasmart.goalpost.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    scaffoldPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(scaffoldPadding)
            .fillMaxSize()
    ) {

    }
}
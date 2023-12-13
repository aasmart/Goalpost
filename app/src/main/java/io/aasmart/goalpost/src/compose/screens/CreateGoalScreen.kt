package io.aasmart.goalpost.src.compose.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.aasmart.goalpost.src.compose.components.Dropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen() {
    var text by remember { mutableStateOf("") }
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = {
                Text(text = "Goal Name")
            },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = {
                Text(text = "Goal Description")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Dropdown(
            label = "Set Goal Reminder Period",
            expanded = expanded,
            menuHeight = 200.dp,
            selectedIndex = selectedIndex,
            items = listOf(),
            onItemClicked = { index -> selectedIndex = index },
            onExpandedChange = { expanded = it; Log.d("why", it.toString()) }
        )
    }
}
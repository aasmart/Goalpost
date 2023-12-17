package io.aasmart.goalpost.src.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiDropdown(
    label: String,
    expanded: Boolean,
    menuHeight: Dp,
    onExpandedChange: (Boolean) -> Unit,
    itemNames: Set<String>,
    selectedIndices: Set<Int>,
    onItemClicked: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: @Composable () -> Unit = {},
) {
    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(!expanded) } ,
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = selectedIndices.joinToString(", ") { itemNames.toList()[it] },
                onValueChange = {},
                label = { Text(text = label) },
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = isError,
                supportingText = supportingText,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            // Use dropdown menu instead of exposed since it's broken
            // https://stackoverflow.com/questions/70642330/cannot-make-exposeddropdownmenu-same-width-as-outlinedtextfield
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .exposedDropdownSize()
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .height(menuHeight)
                    .verticalScroll(rememberScrollState())
                ) {
                    itemNames.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = { onItemClicked(index) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                if(index in selectedIndices)
                                    Icon(Icons.Filled.Check, contentDescription = "Selected")
                            }
                        )
                    }
                }
            }
        }
    }
}
package io.aasmart.goalpost.compose.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    label: String,
    supportingText: @Composable () -> Unit = {},
    expanded: Boolean,
    menuHeight: Dp,
    onExpandedChange: (Boolean) -> Unit,
    selectedIndex: Int,
    items: List<String>,
    onItemClicked: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(!expanded) } ,
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = if (items.isNotEmpty()) items[selectedIndex] else "",
                onValueChange = {},
                label = { Text(text = label) },
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                    items.forEachIndexed {index, item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                onExpandedChange(false)

                                Toast.makeText(
                                    context,
                                    item,
                                    Toast.LENGTH_SHORT
                                ).show()

                                onItemClicked(index)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
package com.example.pokeapi.presentation.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FilterOption(
    type: String,
    isSelected: Boolean,
    onSelected: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isSelected, onCheckedChange = onSelected)
        Text(text = type.replaceFirstChar { it.uppercase() })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterOption() {
    FilterOption(type = "grass", isSelected = true, onSelected = {})
}
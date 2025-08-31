package com.example.pokeapi.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pokeapi.R
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterMenu(
    types: List<String>,
    selectedTypes: Set<String>,
    onSelectedTypesChange: (Set<String>) -> Unit,
    onApply: (Set<String>) -> Unit,
    onReset: () -> Unit,
    showFilterSheet: Boolean,
    onDismiss: () -> Unit
) {
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = rememberModalBottomSheetState(),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_by_type),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (types.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(types.size) { index ->
                                val type = types[index]
                                FilterOption(
                                    type = type,
                                    isSelected = selectedTypes.contains(type),
                                    onSelected = { isSelected ->
                                        onSelectedTypesChange(
                                            if (isSelected) {
                                                selectedTypes + type
                                            } else {
                                                selectedTypes - type
                                            }
                                        )
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            onReset()
                            Log.d("PokemonListScreen", "Resetting filter")
                        }) {
                            Text(stringResource(R.string.reset))
                        }
                        Button(onClick = {
                            onApply(selectedTypes)
                            Log.d("PokemonListScreen", "Applying filter with types: $selectedTypes")
                        }) {
                            Text(stringResource(R.string.apply))
                        }
                    }
                }
            }
        )
    }
}
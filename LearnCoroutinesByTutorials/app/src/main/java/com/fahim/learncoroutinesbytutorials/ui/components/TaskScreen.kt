package com.fahim.learncoroutinesbytutorials.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fahim.learncoroutinesbytutorials.ui.base.UiState

/**
 * Screen for the "long running task" examples: a button and a status line.
 */
@Composable
fun TaskScreen(
    title: String,
    description: String,
    state: UiState<String>,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    ExampleScaffold(title = title, onBack = onBack) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = onStart, enabled = state !is UiState.Loading) {
                Text("Start")
            }
            UiStateContent(state = state, modifier = Modifier.padding(0.dp)) { message ->
                Text(text = message, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
